package com.crphdm.dl2.fragments.library;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.library.LibraryBookDetailsSecondActivity;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 自有资源和共享资源
 */
public class LibraryResourceListFragment extends Fragment {
    public static final int TYPE_SELF = 2;
    public static final int TYPE_SHARE = 3;
    public static final String INTENT_TYPE = "type";
    private static final String TAG = LibraryResourceListFragment.class.getSimpleName();

    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private int mType;
    private int index = 2;

    private List<PgBookForLibraryListEntity> mListData;
    private MyAdapter mAdapter;

    private UserInfo mUserInfoSecond;

    public static LibraryResourceListFragment newInstance(int type) {
        LibraryResourceListFragment libraryResourceListFragment = new LibraryResourceListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(INTENT_TYPE, type);
        libraryResourceListFragment.setArguments(bundle);
        return libraryResourceListFragment;
    }
    //初始化Fragment。
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mType = getArguments().getInt(INTENT_TYPE);
    }
    //初始化Fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_resource, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    //错误显示布局
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    //执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMembers();
    }
    //初始化成员
    public void initMembers() {

        mUserInfoSecond = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new MyAdapter();
        recycler.setAdapter(mAdapter);

        LibraryManager.getInstance().getBookListToSecond("123456", mType, 1, 50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                    @Override
                    public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                        if (pgBookForLibraryListEntities.size() == 0 && lLError != null) {
                            lLError.setVisibility(View.VISIBLE);
                            tvError.setText("暂无数据");
                        } else {
                            lLError.setVisibility(View.GONE);
                            mListData = pgBookForLibraryListEntities;
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (lLError != null)
                            lLError.setVisibility(View.VISIBLE);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        mAdapter.setData(mListData);
                        mAdapter.notifyDataSetChanged();
                    }
                });


        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (mAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && mAdapter.getItemCount() > 0 && (((LinearLayoutManager) recycler.getLayoutManager()).findLastVisibleItemPosition() + 1) == mAdapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    LibraryManager.getInstance().getBookListToSecond("123456", mType, index++, 20)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                                @Override
                                public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                                    if (pgBookForLibraryListEntities == null || pgBookForLibraryListEntities.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgBookForLibraryListEntities);
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    loading = false;
                                    load.setVisibility(View.GONE);
                                }
                            }, new Action0() {
                                @Override
                                public void call() {
                                    loading = false;
                                    load.setVisibility(View.GONE);
                                    mAdapter.notifyDataSetChanged();
                                }
                            });
                }
            }
        });


    }
    //适配器
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgBookForLibraryListEntity> pgBookForLibraryListEntities;

        public void setData(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
            this.pgBookForLibraryListEntities = pgBookForLibraryListEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_institution_library_bookcase, viewGroup, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {

            final PgBookForLibraryListEntity resource = pgBookForLibraryListEntities.get(i);

            Glide.with(getActivity())
                    .load(resource.getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(viewHolder.bookCover);

            viewHolder.author.setText(resource.getAuthor());
            viewHolder.title.setText(resource.getName());
            viewHolder.desc.setText(resource.getIntroduction());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Ln.d("LibraryResourceListFragment:bookId:" + resource.getEntityId());
                    Ln.d("LibraryResourceListFragment:mType:" + mType);
//                    Toast.makeText(getActivity(), "" + resource.getEntityId(), Toast.LENGTH_SHORT).show();

                    if (mUserInfoSecond != null && mUserInfoSecond.getUserid() != 0) {
                        Intent intent = new Intent(getActivity(), LibraryBookDetailsSecondActivity.class);
                        intent.putExtra(Constant.BOOK_ID, resource.getEntityId());
                        intent.putExtra(Constant.LIBRARY_DETAIL_TYPE, mType);
                        startActivity(intent);
                    } else {
                        showDialog("您不是本机构用户，请联系管理员");
//                        Toast.makeText(getActivity(), "您不是本机构用户，请联系管理员", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return pgBookForLibraryListEntities == null ? 0 : pgBookForLibraryListEntities.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.bookCover)
            ImageView bookCover;
            @Bind(R.id.title)
            TextView title;
            @Bind(R.id.author)
            TextView author;
            @Bind(R.id.desc)
            TextView desc;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }
    //显示对话框
    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage(message);
        builder.setPositiveButton("知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

//        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        builder.create();
        builder.show();
    }
    //销毁与Fragment有关的视图，但未与Activity解除绑定
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    //执行该方法时，Fragment处于活动状态，用户可与之交互。
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("图书馆资源页");
    }
    //执行该方法时，Fragment处于暂停状态，但依然可见，用户不能与之交互。
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("图书馆资源页");
    }


}
