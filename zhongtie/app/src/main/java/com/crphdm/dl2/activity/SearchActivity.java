package com.crphdm.dl2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.cloud_bookstore.CloudBookstoreBookDetailActivity;
import com.crphdm.dl2.activity.library.LibraryBookDetailsSecondActivity;
import com.crphdm.dl2.activity.resource.ResourceBookDetailsActivity;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.obj.PgSearchBook;
import com.digital.dl2.business.net.NetHelper;
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
 * 搜索
 */
public class SearchActivity extends AppCompatActivity {
    //搜索
    private SearchView mSearchView;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    //错误信息文字
    @Bind(R.id.tvError)
    TextView tvError;
    //错误布局
    @Bind(R.id.lLError)
    LinearLayout lLError;
    private MyAdapter myAdapter;

    public static final String INTENT_FROM = "INTENT_FROM";
    public static final String INTENT_LIBRARY_TYPE = "INTENT_LIBRARY_TYPE";
    public static final int FROM_STORE = 1;
    public static final int FROM_RESOURCE = 2;
    public static final int FROM_LIBRARY = 3;

    private static final int E_BOOK = 1;
    private static final int PAPER_BOOK = 2;


    private int mFrom;
    private int mLibraryBookType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFrom = getIntent().getIntExtra(INTENT_FROM, FROM_LIBRARY);
        mLibraryBookType = getIntent().getIntExtra(INTENT_LIBRARY_TYPE,1);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter();
        recycler.setAdapter(myAdapter);
    }
    //错误事件
    @OnClick(R.id.lLError)
    public void onErrorClick() {

    }

    //创建Menu菜单的项目
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        mSearchView.setIconified(true);
        mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                NetHelper.Ln.d("QueryText:" + query);

                if (mFrom == FROM_LIBRARY) {
                    //图书馆搜索结果
                    UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    PublicManager.getInstance().searchBook(s, query, mFrom,mLibraryBookType)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<List<PgSearchBook>>() {
                                                @Override
                                                public void call(List<PgSearchBook> pgSearchBooks) {
                                                    if (lLError != null) {
                                                        if (pgSearchBooks.size() == 0) {
                                                            lLError.setVisibility(View.VISIBLE);
                                                            tvError.setText("暂无数据");
                                                        } else {
                                                            myAdapter.setData(pgSearchBooks);
                                                            myAdapter.notifyDataSetChanged();
                                                            lLError.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {
                                                    if (lLError != null) {
                                                        lLError.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (lLError != null) {
                                        lLError.setVisibility(View.VISIBLE);
                                    }
                                }
                            }, new Action0() {
                                @Override
                                public void call() {

                                }
                            });

                } else if (mFrom == FROM_STORE || mFrom == FROM_RESOURCE) {
                    UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeOn(Schedulers.newThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    PublicManager.getInstance().searchBook(s, query, mFrom,0)
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribeOn(Schedulers.newThread())
                                            .subscribe(new Action1<List<PgSearchBook>>() {
                                                @Override
                                                public void call(List<PgSearchBook> pgSearchBooks) {
                                                    if (lLError != null) {
                                                        if (pgSearchBooks.size() == 0) {
                                                            lLError.setVisibility(View.VISIBLE);
                                                            tvError.setText("暂无数据");
                                                        } else {
                                                            lLError.setVisibility(View.GONE);
                                                            myAdapter.setData(pgSearchBooks);
                                                            myAdapter.notifyDataSetChanged();
                                                        }
                                                    }

                                                }
                                            }, new Action1<Throwable>() {
                                                @Override
                                                public void call(Throwable throwable) {
                                                    if (lLError != null) {
                                                        lLError.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });

                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    if (lLError != null) {
                                        lLError.setVisibility(View.VISIBLE);
                                    }
                                }
                            }, new Action0() {
                                @Override
                                public void call() {

                                }
                            });

                } else {
                    Ln.d("SearchActivity:不知道从哪进来的");
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    //每次显示菜单前都会被调用
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    //处理菜单被选中运行后的事件处理
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
    //搜索页面适配器
    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        public List<PgSearchBook> mList;

        public void setData(List<PgSearchBook> list) {
            mList = list;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(getLayoutInflater().inflate(R.layout.item_book_search_activity, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            final PgSearchBook book = mList.get(position);
            Ln.d("SearchActivity:book:" + book.toString());

            holder.title.setText(book.getTitle());
            holder.author.setText(book.getAuthor());
            holder.desc.setText(book.getDescription());

            holder.price.setText(String.valueOf(book.getPrice()));
            holder.oldPrice.setText(String.valueOf(book.getPressPrice()));
            holder.price.setVisibility(View.INVISIBLE);
            holder.oldPrice.setVisibility(View.INVISIBLE);

            Glide.with(SearchActivity.this)
                    .load(book.getThumb())
                    .placeholder(R.drawable.drw_book_default)
                    .into(holder.bookCover);

            if (mFrom == FROM_LIBRARY) {
                holder.type.setText("电子书");
                holder.type.setTextColor(getResources().getColor(R.color.color_text_54c25b));
                holder.type.setBackgroundResource(R.drawable.frame_green);
            } else if (mFrom == FROM_STORE) {
                //不根据type判断是否是实体书   根据book_type判断
                if (book.getBookType() == PAPER_BOOK) {
                    holder.type.setText("实体书");
                    holder.type.setTextColor(getResources().getColor(R.color.color_blue_bg));
                    holder.type.setBackgroundResource(R.drawable.frame_blue);
                } else {
                    holder.type.setText("电子书");
                    holder.type.setTextColor(getResources().getColor(R.color.color_text_54c25b));
                    holder.type.setBackgroundResource(R.drawable.frame_green);
                }
            }


            holder.linearLayoutBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFrom == FROM_LIBRARY) {
                        Intent intent = new Intent(SearchActivity.this, LibraryBookDetailsSecondActivity.class);
                        intent.putExtra(Constant.BOOK_ID, book.getGoodsId());
                        intent.putExtra(Constant.LIBRARY_DETAIL_TYPE, book.getBookType());
                        startActivity(intent);
                    } else if (mFrom == FROM_STORE) {
                        Intent intent = new Intent(SearchActivity.this, CloudBookstoreBookDetailActivity.class);
                        intent.putExtra(Constant.BOOK_ID, book.getGoodsId());
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, book.getBookType());
                        startActivity(intent);
                    } else if (mFrom == FROM_RESOURCE) {
                        Intent intent = new Intent(SearchActivity.this, ResourceBookDetailsActivity.class);
                        intent.putExtra(Constant.BOOK_ID, book.getGoodsId());
                        intent.putExtra(Constant.RESOURCE_TYPE, 0);
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bookCover)
        ImageView bookCover;
        @Bind(R.id.type)
        TextView type;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.rl_title)
        RelativeLayout rlTitle;
        @Bind(R.id.author)
        TextView author;
        @Bind(R.id.desc)
        TextView desc;
        @Bind(R.id.price)
        TextView price;
        @Bind(R.id.old_price)
        TextView oldPrice;
        @Bind(R.id.linear_layout_book)
        LinearLayout linearLayoutBook;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    //恢复
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("搜索页");
        MobclickAgent.onResume(this);
    }
    //恢复
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("搜索页");
        MobclickAgent.onPause(this);
    }
}
