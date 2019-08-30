package com.crphdm.dl2.activity.resource;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.digital.dl2.business.core.manager.LibraryManager;
import com.digital.dl2.business.core.obj.PgResourcesListEntity;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
/**
 * 资源 机构列表页
 */
public class ResourceOrgListActivity extends AppCompatActivity {

    private static final String TAG = ResourceOrgListActivity.class.getSimpleName();
    //资源 机构列表页
    @Bind(R.id.rv_2_resource_org_list)
    RecyclerView mRecyclerView;
    //加载
    @Bind(R.id.load)
    FrameLayout load;
    private Context mContext;
    private int mInstitutionId;
    private int mType;

    private UserInfo mUserInfo;
    private String mToken;
    private ProgressDialog mProgressDialog;
    private int index = 2;
    private List<PgResourcesListEntity> mListData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resource_org_list);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mInstitutionId = getIntent().getIntExtra(Constant.INSTITUTION_ID,0);
        mType = getIntent().getIntExtra(Constant.RESOURCE_TYPE,0);

        mContext = this;

        Ln.d("ResourceOrgListActivity:onCreate:mInstitutionId:" + mInstitutionId);
        Ln.d("ResourceOrgListActivity:onCreate:mType:" + mType);

        if(mProgressDialog == null){
            mProgressDialog = ProgressDialog.show(mContext, null, "加载中...");
        }

        mUserInfo = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mToken = s;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        initMembers(mInstitutionId);
                    }
                });

    }
    //初始化成员
    private void initMembers(final int orgId){
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL_LIST));

        final MyAdapter adapter = new MyAdapter();
        mRecyclerView.setAdapter(adapter);

        LibraryManager.getInstance().getResourcesListByLibraryId(mUserInfo.getUserid(),mToken,mType,orgId,1,50)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgResourcesListEntity>>() {
                    @Override
                    public void call(List<PgResourcesListEntity> pgResourcesListEntities) {
                        Ln.d("ResourceOrgListActivity:list:" + pgResourcesListEntities);
                        mListData = pgResourcesListEntities;
                        adapter.setData(mListData);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //错误
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        throwable.printStackTrace();
//                        Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = false;
            boolean end = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (end)
                    return;
                if (adapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && !loading
                        && adapter.getItemCount() > 0 && (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition() + 1) == adapter.getItemCount()) {
                    loading = true;
                    load.setVisibility(View.VISIBLE);

                    LibraryManager.getInstance().getResourcesListByLibraryId(mUserInfo.getUserid(), mToken, mType, orgId, index++, 20)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<List<PgResourcesListEntity>>() {
                                @Override
                                public void call(List<PgResourcesListEntity> pgResourcesListEntities) {
                                    if (pgResourcesListEntities == null || pgResourcesListEntities.isEmpty()) {
                                        end = true;
                                        return;
                                    }
                                    mListData.addAll(pgResourcesListEntities);
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
                                    adapter.notifyDataSetChanged();

                                }
                            });

                }
            }
        });


    }
    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    //机构列表页
    class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<PgResourcesListEntity> pgBookForLibraryListEntities;

        public void setData(List<PgResourcesListEntity> pgBookForLibraryListEntities) {
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
            if(getItemCount() != 0){
                Glide.with(mContext)
                        .load(pgBookForLibraryListEntities.get(i).getFrontCover())
                        .placeholder(R.drawable.drw_book_default)
                        .into(viewHolder.bookCover);

                viewHolder.title.setText(pgBookForLibraryListEntities.get(i).getName());
                viewHolder.author.setText("作者：" + pgBookForLibraryListEntities.get(i).getAuthor());
                viewHolder.desc.setText(pgBookForLibraryListEntities.get(i).getIntroduction());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ResourceOrgListActivity.this,ResourceBookDetailsActivity.class);
                        intent.putExtra(Constant.BOOK_ID,pgBookForLibraryListEntities.get(i).getEntityId());
                        intent.putExtra(Constant.RESOURCE_TYPE,mType);
                        startActivity(intent);
                    }
                });
            }
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
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("机构列表页");
        MobclickAgent.onResume(this);
    }
    //暂停
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("机构列表页");
        MobclickAgent.onPause(this);
    }

}
