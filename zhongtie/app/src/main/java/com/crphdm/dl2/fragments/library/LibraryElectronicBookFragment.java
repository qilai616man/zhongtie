package com.crphdm.dl2.fragments.library;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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


public class LibraryElectronicBookFragment extends Fragment {
    //图书馆 电子书
    private static final String TAG = LibraryElectronicBookFragment.class.getSimpleName();

    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private List<PgBookForLibraryListEntity> mListData;
    private MyAdapter mAdapter;

    private UserInfo mUserInfoSecond;
    private UserInfo mUserInfoFirst;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_electronic_book, container, false);
        ButterKnife.bind(this, view);
        mUserInfoFirst = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        return view;
    }

    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initMembers();
    }

    private ProgressDialog mProgressDialog;
    private int index = 2;

    public void initMembers() {

        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
        }

        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

        mAdapter = new MyAdapter();

        mUserInfoSecond = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        LibraryManager.getInstance().getBookListToSecond("123456", Constant.LIBRARY_BOOK_TYPE_BOOK, 1, 20)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                    @Override
                    public void call(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
                        mListData = pgBookForLibraryListEntities;
                        if (lLError != null) {
                            if (pgBookForLibraryListEntities.size() == 0) {
                                lLError.setVisibility(View.VISIBLE);
                                tvError.setText("暂无数据");
                            } else {
                                lLError.setVisibility(View.GONE);
                            }
                        }

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        lLError.setVisibility(View.VISIBLE);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        mAdapter.setData(mListData);
                        if (recycler != null) {
                            recycler.setAdapter(mAdapter);
                        }
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

                    LibraryManager.getInstance().getBookListToSecond("123456", Constant.LIBRARY_BOOK_TYPE_BOOK, index++, 20)
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


    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgBookForLibraryListEntity> pgBookForLibraryListEntities;

        public void setData(List<PgBookForLibraryListEntity> pgBookForLibraryListEntities) {
            this.pgBookForLibraryListEntities = pgBookForLibraryListEntities;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).
                    inflate(R.layout.item_institution_library_bookcase, viewGroup, false);

            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int i) {
            final PgBookForLibraryListEntity book = pgBookForLibraryListEntities.get(i);

            Glide.with(getActivity())
                    .load(book.getFrontCover())
                    .placeholder(R.drawable.drw_book_default)
                    .into(viewHolder.bookCover);

            viewHolder.author.setText(book.getAuthor());
            viewHolder.title.setText(book.getName());
            viewHolder.desc.setText(book.getIntroduction());

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Toast.makeText(getActivity(),"图书馆首页 电子书详情",Toast.LENGTH_SHORT).show();
                    Ln.d("LibraryResourceListFragment:bookId:" + book.getEntityId());
//                    Toast.makeText(getContext(),"LibraryResourceListFragment:bookId:" + book.getEntityId(),Toast.LENGTH_SHORT).show();
                    if (mUserInfoSecond != null && mUserInfoSecond.getUserid() != 0) {
//                        Toast.makeText(getActivity(), "" + book.getEntityId(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LibraryBookDetailsSecondActivity.class);
                        intent.putExtra(Constant.BOOK_ID, book.getEntityId());
                        intent.putExtra(Constant.LIBRARY_DETAIL_TYPE, Constant.LIBRARY_DETAIL_E_BOOK);
                        startActivity(intent);
//                    startActivity(new Intent(getActivity(), LibraryBookDetailsFirstActivity.class));
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

        builder.create();
        builder.show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("图书馆电子书页");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("图书馆电子书页");
    }
}
