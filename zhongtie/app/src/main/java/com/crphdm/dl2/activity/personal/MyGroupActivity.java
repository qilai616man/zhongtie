package com.crphdm.dl2.activity.personal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crphdm.dl2.R;
import com.crphdm.dl2.utils.Constant;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.obj.PgGroup;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyGroupActivity extends AppCompatActivity {
    private List<PgGroup> mList;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;
    private MyAdapter mAdapter;

    private Handler mHandler;
    private String mGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_group);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new MyAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(mAdapter);

        mHandler = new Handler();

        displayGroup();
    }


    @OnClick(R.id.lLError)
    public void onErrorClick() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_group, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        else if (item.getItemId() == R.id.action_create_group) {
            //添加分组
            createGroup();
        }
        return super.onOptionsItemSelected(item);
    }

    private void createGroup() {
        final EditText editText = new EditText(this);
        editText.setHint("输入分组名");
        editText.setSingleLine();
        editText.setPadding(50, 100, 50, 50);

        new AlertDialog.Builder(this)
                .setView(editText)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mGroupName = editText.getText().toString();
                        if (!mGroupName.equals("")) {
                            if (!isRepeat(mGroupName)) {
                                showToast("添加分组成功");
                                Ln.d("MyGroupActivity:createGroup:name:" + mGroupName);
                                PgGroup pgGroup = new PgGroup();
                                pgGroup.setName(editText.getText().toString());

                                BookshelfManager.getInstance().insertGroup(editText.getText().toString());
                                displayGroup();

                                sendBroadcastToHomeData();
                            } else {
                                showToast("分组已存在，请重新输入");
                            }

                        } else {
                            showToast("分组名称不能为空");
                        }

                        dialogInterface.dismiss();


                    }
                }).show();
    }

    private void displayGroup() {
        mList = BookshelfManager.getInstance().queryAllGroup();

        Ln.d("MyGroupActivity:displayGroup:mList:" + mList);
        if (mList.size() == 0) {
            lLError.setVisibility(View.VISIBLE);
            tvError.setText("暂无数据");
        } else {
            lLError.setVisibility(View.GONE);
        }

        mAdapter.notifyDataSetChanged();
    }

    private boolean isRepeat(String name) {
        Ln.d("MyGroupActivity:isRepeat:name:" + name);

        if (mList != null && !mList.isEmpty()) {
            for (int i = 0; i < mList.size(); i++) {
                String group = mList.get(i).getName();
                Ln.d("MyGroupActivity:isRepeat:group:" + group);
                if (name != null && name.equals(group)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void sendBroadcastToHomeData() {
        Intent intent = new Intent();
        intent.setAction(Constant.ACTION_REFRESH_DATA);
        sendBroadcast(intent);
    }

    private void showToast(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyGroupActivity.this, text, Toast.LENGTH_LONG).show();
            }
        });
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
    }

    public class MyAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(getLayoutInflater().inflate(R.layout.item_group, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final PgGroup pgGroup = mList.get(position);
            holder.groupName.setText(pgGroup.getName());
//            if (position == 0)
//                holder.delete.setVisibility(View.INVISIBLE);
//            else
//                holder.delete.setVisibility(View.VISIBLE);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BookshelfManager.getInstance().deleteGroup(pgGroup.getId());
                    displayGroup();

                    sendBroadcastToHomeData();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.group_name)
        TextView groupName;
        @Bind(R.id.delete)
        Button delete;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

//            delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (getAdapterPosition() >= mList.size() || getAdapterPosition() < 0)
//                        return;
//
//                    mList.remove(getAdapterPosition());
//                    mAdapter.notifyItemRemoved(getAdapterPosition());
//                }
//            });
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("我的分组页");
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("我的分组页");
        MobclickAgent.onPause(this);
    }
}
