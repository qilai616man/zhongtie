package com.crphdm.dl2.fragments.bookcase;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.bumptech.glide.Glide;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.WebActivity;
import com.crphdm.dl2.activity.cloud_bookstore.CloudBookstoreBookDetailActivity;
import com.crphdm.dl2.activity.personal.MyGroupActivity;
import com.crphdm.dl2.service.MyService;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.UserModuleImpl;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.views.BeautifulProgressbar;
import com.crphdm.dl2.views.DividerItemDecoration;
import com.crphdm.dl2.views.ParallaxRecyclerAdapter;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.obj.PgAdvertisement;
import com.digital.dl2.business.core.obj.PgBookshelfItem;
import com.digital.dl2.business.core.obj.PgGroup;
import com.digital.dl2.business.core.obj.PgResult;
import com.digital.dl2.business.database.DatabaseManager;
import com.digital.dl2.business.database.ProvisionalityDatabaseHelper;
import com.digital.dl2.business.database.obj.DbBookshelfEntity;
import com.digital.dl2.business.util.DownloadEvent;
import com.github.why168.LoopViewPagerLayout;
import com.github.why168.listener.OnBannerItemClickListener;
import com.github.why168.loader.OnDefaultImageViewLoader;
import com.github.why168.modle.BannerInfo;
import com.goyourfly.gdownloader.GDownloader;
import com.goyourfly.gdownloader.db.DbDownloadExt;
import com.goyourfly.gdownloader.helper.DownloadHelper;
import com.goyourfly.gdownloader.utils.ErrorUtils;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import org.geometerplus.fbreader.book.Book;
import org.geometerplus.fbreader.bookmodel.BookModel;
import org.geometerplus.fbreader.bookmodel.BookReadingException;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.acra.ACRA.log;

public class BookcaseFragment extends Fragment {
    //书架
    private static final int COUNT = 50;
    @Bind(R.id.load)
    FrameLayout load;
    @Bind(R.id.tvError)
    TextView tvError;
    @Bind(R.id.lLError)
    LinearLayout lLError;

    private String mGroup = Constant.GROUP_ALL;
    @Bind(R.id.recycler)
    RecyclerView mRecycler;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private ParallaxRecyclerAdapter<PgBookshelfItem> mAdapter;
    private List<PgBookshelfItem> mList = new ArrayList<>();
    private View mHeader;

    private DownloadHelper.DownloadListener mListener;

    private int mNetState;
    private boolean isAutoSelect = true;
    private Context mContext;

    private List<PgAdvertisement> mUrlList = new ArrayList<>();
    private List<String> mGroupList = new ArrayList<>();
    private List<String> mSelectGroupList = new ArrayList<>();

    private UserInfo mUserInfoFirst;
    private UserInfo mUserInfoSecond;
    private String mUserName;

    private String mUUIDFirst;
    private String mUUIDSecond;

    private String mToken;
//    private String[] uuids;

    private int mDownloadBookId = 0;

    private Handler mHandler = new Handler();

    private int spinnerPosition = 0;

    private HashMap<String, Integer> postionCache = new HashMap<>();
    private HashMap<String, Integer> progressChanged = new HashMap<>();

    public BookcaseFragment() {
        // Required empty public constructor
    }

    public static BookcaseFragment newInstance(int netState) {
        BookcaseFragment fragment = new BookcaseFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetState = getArguments().getInt(ARG_NET_STATE);
        }
        EventBus.getDefault().register(this);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    private void refreshNetState() {
        UserModuleImpl.getInstance().getNetState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mNetState = integer;
//                        Toast.makeText(WelcomeActivity.this, "mNetState:" + mNetState, Toast.LENGTH_LONG).show();
                        Ln.d("WelcomeActivity:getNetState:integer:" + integer);
                        UserModule.getInstance().setNetStateLocal(integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }


    List<Book> list=new ArrayList<>();
    List<Long> idList=new ArrayList<>();
    public void onEventMainThread(Book book) {
        long id=book.getId();
        if(!idList.contains(id)){
            idList.add(id);
            list.add(book);
            Log.i("size",list.size()+"===");
        }
        try {
            BookModel bookModel=BookModel.createModel(book);
            int count= bookModel.getTextModel().search("的",0,1000,false);
            Log.i("size","count=="+count);
        } catch (BookReadingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookcase, container, false);
        ButterKnife.bind(this, view);

        initMembers();
        return view;
    }
    @OnClick(R.id.lLError)
    public void onErrorClick() {
        initMembers();
    }




    private void initMembers() {
        mContext = getActivity();

        mAdapter = new ParallaxRecyclerAdapter<>(mList);
        mAdapter.setShouldClipView(false);
//        mRecycler.getItemAnimator().setSupportsChangeAnimations(false);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        setupRecycler();

        mHeader = getActivity().getLayoutInflater().inflate(R.layout.loop_view_pager, null);
        final ImageView advertisementError = (ImageView) mHeader.findViewById(R.id.iv_advertisement_error);
        mAdapter.setParallaxHeader(mHeader, mRecycler);

        PublicManager.getInstance().getAdvertisementsByPage(3)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<List<PgAdvertisement>>() {
                    @Override
                    public void call(List<PgAdvertisement> urlList) {
                        mUrlList = urlList;

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        advertisementError.setVisibility(View.VISIBLE);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        setupHeader(mUrlList);
                        advertisementError.setVisibility(View.GONE);
                    }
                });

//        initBookshelfData(mGroup);

        mUserInfoFirst = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        mUserInfoSecond = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        if(mUserInfoFirst != null){
            mUserName = mUserInfoFirst.getUsername();
        }else {
            mUserName = mUserInfoSecond.getUsername();
        }

        if (mUserInfoFirst != null) {
            mUUIDFirst = mUserInfoFirst.getFirstKey();
            mUUIDSecond = mUserInfoFirst.getSecondKey();
            Ln.d("BookcaseFragment:initMembers:mUUIDFirst:" + mUUIDFirst);
            Ln.d("BookcaseFragment:initMembers:mUUIDSecond:" + mUUIDSecond);
        }


        refreshData();
        //注册广播
        registerAlarmServiceReceiver();
    }
    private ProgressDialog mProgressDialog;
    private void refreshData() {
        mList = BookshelfManager.getInstance().getBookShelfItemList(Constant.BOOKSHELF_TYPE_ALL, 0, COUNT);
        Ln.d("BookcaseFragment:initBookshelfData:getBookShelfItemList:" + mList);

        EventBus.getDefault().post(mList);
        int i = 0;
        for (PgBookshelfItem p : mList) {
            postionCache.put(p.getDownloadUrl(), i);
            i++;
        }

        //所有分组信息
        setupSpinner(getGroupListData());
        mAdapter.notifyDataSetChanged();
    }

    private void initBookshelfData(String group) {
        if (group.equals("全部")) {
//            final String fileUrl = Environment.getExternalStorageDirectory() + "/crphdm/download/t200100005_1359_mobi.tdp";
//            final String readUrl = Environment.getExternalStorageDirectory() + "/crphdm/read/1359/";
//            String[] uuids = new String[]{"de4d3618d2c7b5dcba77cf7a2528d11c144c98da4719a997ca3e1220a7fe8e6fc3bdb0a35b3f29801c4ae939c96a8f85"};
//            BookUtils.openTdpBook(
//                    fileUrl,
//                    readUrl,
//                    uuids,
//                    mContext);
            mList = BookshelfManager.getInstance().getBookShelfItemList(Constant.BOOKSHELF_TYPE_ALL, 0, COUNT);
        } else if (group.equals("借阅图书")) {
            mList = BookshelfManager.getInstance().getBookShelfItemList(Constant.BOOKSHELF_TYPE_BORROWED, 0, COUNT);
        } else if (group.equals("购买图书")) {
            mList = BookshelfManager.getInstance().getBookShelfItemList(Constant.BOOKSHELF_TYPE_BUY, 0, COUNT);
        } else if (group.equals("资源")) {
            mList = BookshelfManager.getInstance().getBookShelfItemList(Constant.BOOKSHELF_TYPE_RESOURCES, 0, COUNT);
        } else {
            mList = BookshelfManager.getInstance().getBookShelfItemListByGroup(group, 0, COUNT);
        }
        Ln.d("BookcaseFragment:initBookshelfData:mList:" + mList);
        Ln.d("BookcaseFragment:initBookshelfData:mList.size:" + mList.size());
        mAdapter.setData(mList);
        Log.i("CC",mList.size()+"----");
        mAdapter.notifyDataSetChanged();
    }

    public void onEventMainThread(DownloadEvent event) {
        if (!postionCache.containsKey(event.url))
            return;
        int position = postionCache.get(event.url);
        PgBookshelfItem p = mList.get(position);
        p.setDownloadState(event.state);
        switch (event.event) {
            case DownloadEvent.EVENT_PROGRESS:
                int progress = (int) (event.downloadedBytes * 100 / event.totalLength);
                p.setDownloadProgress((float) event.downloadedBytes / (float) event.totalLength);
                if (!progressChanged.containsKey(event.url) || progressChanged.get(event.url) != progress) {
                    progressChanged.put(event.url, progress);
                    mAdapter.notifyItemChanged(position + 1);
                }
                break;
            default:
                mAdapter.notifyItemChanged(position + 1);
                break;
        }
    }

    private List<String> getGroupListData() {
        Ln.d("BookcaseFragment:getGroupListData:mGroupList(one):" + mGroupList);
        mGroupList.clear();
        mSelectGroupList.clear();
        Ln.d("BookcaseFragment:getGroupListData:mGroupList(two):" + mGroupList);

        mGroupList.add("创建分组");
        mGroupList.add("全部");
        mGroupList.add("借阅图书");

        mSelectGroupList.add("全部");

        Ln.d("BookcaseFragment:getGroupListData:mGroupList(three):" + mGroupList);

        List<PgGroup> groupList = BookshelfManager.getInstance().queryAllGroup();

        Ln.d("BookcaseFragment:getGroupListData:groupList:" + groupList);

        if (groupList != null && !groupList.isEmpty()) {
            for (PgGroup group : groupList) {
                Ln.d("BookcaseFragment:getGroupListData:group:" + group.toString());
                mGroupList.add(group.getName());
                mSelectGroupList.add(group.getName());
                Ln.d("BookcaseFragment:getGroupListData:mGroupList(four):" + mGroupList);
            }
        }

        Ln.d("BookcaseFragment:getGroupListData:mGroupList(five):" + mGroupList);
        return mGroupList;
    }

    private void setupHeader(List<PgAdvertisement> list) {
//        Ln.d("SetupHeader:" + new Gson().toJson(list));
//        EndlessViewPager loopViewPager = (EndlessViewPager) mHeader.findViewById(R.id.viewPager);
        LoopViewPagerLayout loopViewPager= (LoopViewPagerLayout) mHeader.findViewById(R.id.viewPager);

        loopViewPager.initializeData(mContext);
        ArrayList<BannerInfo> bannerInfos=new ArrayList<>();
        for(PgAdvertisement pgAdvertisement:list){
            bannerInfos.add(new BannerInfo<PgAdvertisement>(pgAdvertisement,null));
        }
        loopViewPager.setOnLoadImageViewListener(new OnDefaultImageViewLoader() {
            @Override
            public void onLoadImageView(ImageView imageView, Object parameter) {
                imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Glide.with(getActivity())
                        .load(((PgAdvertisement)parameter).getImageUrl())
                        .centerCrop()
                        .into(imageView);
            }
        });
        loopViewPager.setOnBannerItemClickListener(new OnBannerItemClickListener() {
            @Override
            public void onBannerClick(int index, ArrayList<BannerInfo> banner) {
                PgAdvertisement advertisement= (PgAdvertisement) banner.get(index).data;

                int type = advertisement.getType();
                Log.i("cccccccccccccc","type========"+type);
                if(type == 1){//广告
                    Intent intent =  new Intent(getActivity(), WebActivity.class);
                    intent.putExtra(WebActivity.INTENT_POST_URL,advertisement.getLinkUrl());
                    startActivity(intent);
                }else {//图书
                    Intent intent = new Intent(getActivity(), CloudBookstoreBookDetailActivity.class);
                    intent.putExtra(Constant.BOOK_ID, advertisement.getBookId());
//                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, mCardId);
                    if(type == 2){
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
                    }else {
                        intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_POD);
                    }
                    startActivity(intent);
                }

            }
        });
        loopViewPager.setLoopData(bannerInfos);

//        AdAdapter pagerAdapter = new AdAdapter();
//        pagerAdapter.setItem(list);
//        loopViewPager.setAdapter(pagerAdapter);
//
//        EndlessCirclePageIndicator indicator = (EndlessCirclePageIndicator) mHeader.findViewById(R.id.viewPagerIndicator);
//        indicator.setViewPager(loopViewPager);
    }

    private void setupSpinner(final List<String> list) {
        final Spinner spinner = (Spinner) mHeader.findViewById(R.id.spinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, list);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Ln.d("BookcaseFragment:setupSpinner:mGroup:" + mGroup);

                if (position == 0) {
                    Intent intent = new Intent(getActivity(), MyGroupActivity.class);
                    startActivity(intent);

                    mGroup = list.get(spinnerPosition);
                    spinner.setSelection(spinnerPosition);
                    initBookshelfData(mGroup);
                } else {
                    spinnerPosition = position;
                    mGroup = list.get(position);
                    initBookshelfData(mGroup);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private BroadcastReceiver mRefreshDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Ln.d("BookcaseFragment:BroadcastReceiver:");
            if (Constant.ACTION_REFRESH_DATA.equals(action)) {
                refreshData();
            }
        }
    };

    // 注册广播
    private void registerAlarmServiceReceiver() {
        Ln.d("BookcaseFragment:registerAlarmServiceReceiver(註冊廣播)");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_REFRESH_DATA);
        mContext.registerReceiver(mRefreshDataReceiver, intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();

        MobclickAgent.onPageStart("书架页");

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("书架页");

    }

    private void showGroupDialog(final String[] items, final int id) {
        new AlertDialog.Builder(getActivity())
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = items[i];
                        BookshelfManager.getInstance().updateBookByGroup(id, name);

                    }
                }).show();
    }

    private void setupRecycler() {
        mAdapter.implementRecyclerAdapterMethods(new ParallaxRecyclerAdapter.RecyclerAdapterMethods() {
            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int i) {
                final ViewHolder holder = (ViewHolder) viewHolder;
                final PgBookshelfItem pgBookshelfItem = mList.get(i);

                Ln.d("BookcaseFragment:itemView:pgBookshelfItem:" + pgBookshelfItem.toString());

                holder.author.setText(pgBookshelfItem.getAuthor());
                holder.desc.setText(pgBookshelfItem.getIntroduction());
                holder.title.setText(pgBookshelfItem.getName());

                if (pgBookshelfItem.getDownloadProgress() != 0) {
                    if (pgBookshelfItem.getDownloadProgress() < 0.01) {
                        holder.progressbar.to(0.01F);
                    } else {
                        holder.progressbar.to(pgBookshelfItem.getDownloadProgress());
                    }
                }

                Glide.with(BookcaseFragment.this)
                        .load(pgBookshelfItem.getFrontCover())
                        .placeholder(R.drawable.drw_book_default)
                        .into(holder.bookCover);

                switch (pgBookshelfItem.getDownloadState()) {
                    case DbDownloadExt.DOWNLOAD_STATE_NOT_DOWNLOAD:
                        holder.progressbar.setVisibility(View.GONE);
                        break;
                    case DbDownloadExt.DOWNLOAD_STATE_PREPARING:
                        holder.progressbar.setVisibility(View.VISIBLE);
                        break;
                    case DbDownloadExt.DOWNLOAD_STATE_DOWNLOADING:
                        holder.progressbar.setVisibility(View.VISIBLE);
                        break;
                    case DbDownloadExt.DOWNLOAD_STATE_PAUSE:
                        holder.progressbar.setVisibility(View.VISIBLE);
                        break;
                    case DbDownloadExt.DOWNLOAD_STATE_WAITING:
                        holder.progressbar.setVisibility(View.VISIBLE);
                        break;
                    case DbDownloadExt.DOWNLOAD_STATE_DOWNLOADED:
                        holder.progressbar.setVisibility(View.GONE);
                        break;
                    case DbDownloadExt.DOWNLOAD_STATE_ERROR:
                        holder.progressbar.setVisibility(View.VISIBLE);
                        break;
                    default:
                        holder.progressbar.setVisibility(View.VISIBLE);
                        break;
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final PgBookshelfItem book = mList.get(i);
                        Ln.d("BookcaseFragment:itemView:book:" + book);
                        //阅读
                        if (BookshelfManager.getInstance().getBookById(book.getEntityId()) == null) {
                            BookshelfManager.getInstance().insertBook(book);
                        }
                        long nowTime = System.currentTimeMillis()/1000l;
                        int CanBorrowTime=-1;
                        try {
                            log.i("ccccccc",mUserName);
                            CanBorrowTime=DatabaseManager.getInstance().queryExceedTime(mUserName);
//                            Toast.makeText(mContext,"canborrowtime=="+CanBorrowTime+"=="+mUserName,Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            Log.i("cccc","eee="+e.getMessage());
//                            Toast.makeText(mContext,"e=="+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }

                        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
//                            Toast.makeText(mContext, "可借阅时长："+CanBorrowTime, Toast.LENGTH_LONG).show();
                            if(mUserName.equals("t200100001")){//判断是否是展示版用户
                                if(nowTime - book.getBorrowTime() > 60 * 15){
                                    //借阅的图书到期归还
                                    getReturnBook(book);
                                    File file=new File(pgBookshelfItem.getLocalUrl());
                                    deleteFile(file);
                                }else{
                                    //下载阅读图书
                                    getDownloader(pgBookshelfItem, book);

                                }
                            }else if(CanBorrowTime!=0&&CanBorrowTime!=-1){
                                /**
                                 * 2018.03.21  ymd  --已开------暂注释书架归还图书，测试用
                                 */
                                if(nowTime - book.getBorrowTime() > CanBorrowTime){
                                    //借阅的图书到期归还
                                    getReturnBook(book);
                                    File file=new File(pgBookshelfItem.getLocalUrl());
                                    deleteFile(file);
                                }else{
                                    //下载阅读图书
                                    getDownloader(pgBookshelfItem, book);
                                }
                            }else{
                                /**
                                 * 2017.08.08 ymd
                                 * 判断上海发行分部的部分xml图书bookid和借阅时限
                                 */
                                String bookid = book.getEntityId()+"";
//                                Toast.makeText(mContext, "----"+ bookid, Toast.LENGTH_SHORT).show();
                                int time  = DatabaseManager.getInstance().queryjsobById(bookid);
//                                Toast.makeText(mContext, time+"----"+ bookid, Toast.LENGTH_SHORT).show();
                                if(time != 0){
                                    if(nowTime - book.getBorrowTime() > 60 * time){
                                        //借阅的图书到期归还
                                        getReturnBook(book);
                                        File file=new File(pgBookshelfItem.getLocalUrl());
                                        deleteFile(file);
                                    }else{
                                        //下载阅读图书
                                        getDownloader(pgBookshelfItem, book);
                                    }
                                }else{
                                    if(nowTime - book.getBorrowTime() > 60 * 60 * 24 * 15){
                                        //借阅的图书到期归还
                                        getReturnBook(book);
                                        File file=new File(pgBookshelfItem.getLocalUrl());
                                        deleteFile(file);
                                    }else{
                                        //下载阅读图书
                                        getDownloader(pgBookshelfItem, book);

                                    }
                                }
                            }
                        }else{
                            if (PublicManager.getInstance().isDownloadOk(book.getDownloadUrl())) {//已经下载
                                log.i("dddd","false");
                                /**
                                 * 2017.10.31 ymd
                                 * 判断用户是否为成都无借阅时限用户
                                 */
                                try{
                                    if(DatabaseManager.getInstance().queryuserjson("'"+mUserName+"'")){
                                        log.i("dddd","true");
                                        BookshelfManager.getInstance().updateBookBorrowTime(book.getEntityId());
                                    }else{
                                        log.i("dddd","false");
                                        /**
                                         * 2017.10.31  ymd
                                         * 借阅30天图书处理canborrwtime-60 * 60 * 24 *3
                                         */
                                        if(CanBorrowTime==-1){
                                            CanBorrowTime=30*60*60*24;
                                        }
                                        if(CanBorrowTime==0){

                                        }else if(nowTime - book.getBorrowTime() < CanBorrowTime){
                                            long residueTime=CanBorrowTime-nowTime+book.getBorrowTime();
                                            if(residueTime<3*60*60*24&&residueTime>0){
                                                int tian=0;
                                                if(residueTime<24*60*60*3){
                                                    tian=3;
                                                }
                                                if(residueTime<24*60*60*2){
                                                    tian=2;
                                                }
                                                if(residueTime<24*60*60){
                                                    tian=1;
                                                }
                                                Toast.makeText(mContext, "当前图书将于"+tian+"天内到期，请续借", Toast.LENGTH_LONG).show();
                                            }
                                        }else {
                                            Toast.makeText(mContext, "您的借阅时间已到期，请再次借阅！", Toast.LENGTH_LONG).show();
                                            refreshNetState();
                                            int state=UserModule.getInstance().getNetStateLocal();
                                            if(state==Constant.NET_STATE_SECOND_LEVEL||state==Constant.NET_STATE_ALL){
                                                if (mUserInfoSecond != null) returnbook(pgBookshelfItem);
                                            }else{
                                                try{

//                                                    BookshelfManager.getInstance().removeBookById(pgBookshelfItem.getEntityId());
//                                                    refreshData();
//                                                    Toast.makeText(mContext, "kkk1", Toast.LENGTH_LONG).show();
//                                                    DatabaseManager.getInstance().deleteBook(book.getEntityId());
                                                    initBookshelfData(mGroup);
//                                                    Toast.makeText(mContext, "kkk2", Toast.LENGTH_LONG).show();

                                                    File file=new File(book.getDownloadUrl());
//                                                    Toast.makeText(mContext, "kkk3", Toast.LENGTH_LONG).show();
                                                    deleteFile(file);
                                                    file = new File(book.getLocalUrl());
                                                    deleteFile(file);
                                                }catch(Exception e){
                                                    Log.i("cccc",e.getMessage());
                                                }

                                            }
                                        }
//
                                    }
                                }catch(Exception e){
                                    Log.i("ddddd",e.getMessage());
                                }
//                                Toast.makeText(mContext, "借阅时间:"+book.getBorrowTime(), Toast.LENGTH_LONG).show();
                                final String fileUrl = PublicManager.getInstance().getFilePath(pgBookshelfItem.getDownloadUrl());
                                final String readUrl = pgBookshelfItem.getLocalUrl();
                                String[] uuids = UserModule.getInstance().getUUIDs();

                                Ln.d("BookcaseFragment:itemView:阅读:download:" + fileUrl);
                                Ln.d("BookcaseFragment:itemView:阅读:read:" + readUrl);
                                Ln.d("BookcaseFragment:itemView:阅读:uuids:" + Arrays.toString(uuids));
                                Log.i("阅读:download:" , fileUrl);
                                Log.i("阅读:read:" , readUrl);
                                Log.i("阅读:uuids:" , Arrays.toString(uuids));
//                                Toast.makeText(mContext,"阅读:uuids:" + Arrays.toString(uuids),Toast.LENGTH_SHORT).show();
//                                Toast.makeText(mContext, "222可借阅时长："+CanBorrowTime, Toast.LENGTH_LONG).show();
                                if (fileUrl != null && readUrl != null) {
//                                File file = new File(pgBookshelfItem.getLocalUrl());
                                    int unzipState = BookshelfManager.getInstance().getBookById(pgBookshelfItem.getEntityId()).getUnzipState();
                                    Ln.d("BookcaseFragment:itemView:阅读:unzipState:" + unzipState);
                                    if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_SUCCESS) {
                                        if (fileUrl.endsWith("tdp")) {

                                            if (uuids.length != 0) {
                                                String succeed = BookUtils.openTdpBook(
                                                        fileUrl,
                                                        readUrl,
                                                        uuids,
                                                        mContext);
                                                Ln.d("BookcaseFragment:readBook:阅读:succeed:" + succeed);
//                                                Toast.makeText(mContext, "BookcaseFragment:readBook:阅读:succeed:" + succeed, Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(getActivity(), "用户信息出错,请重新登录", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            BookUtils.openFile(
                                                    fileUrl,
                                                    mContext);
                                            Ln.d("BookcaseFragment:readBook:阅读:打开自有资源");
                                        }
                                    } else if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_ING) {
                                        Toast.makeText(getActivity(), "文件正在解压,请稍等", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Ln.d("BookcaseFragment:itemView:文件未解压");
                                        Toast.makeText(getActivity(), "文件解压失败,正在重新尝试", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getActivity(), MyService.class);
                                        intent.putExtra(MyService.INTENT_CMD, MyService.CMD_UN_ZIP_FILE);
                                        intent.putExtra(Constant.BOOK_ID, pgBookshelfItem.getEntityId());
                                        mContext.startService(intent);
                                    }
                                } else {
                                    Ln.d("BookcaseFragment:itemView:文件路径为空");
                                    mAdapter.notifyDataSetChanged();
                                    Toast.makeText(getActivity(), "获取路径失败,请重新尝试", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Ln.d("BookcaseFragment:itemView:未下载下载");
                                mDownloadBookId = pgBookshelfItem.getEntityId();

                                //借阅的图书  如果不在二级网络下  或者是二级没登录  则不下载
                                if (pgBookshelfItem.getSource() == Constant.BOOK_SOURCE_BORROWING) {
                                    //二级网络或者是全网络
                                    if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                                            UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                                        if (mUserInfoSecond != null) {
                                            PublicManager.getInstance().downloadBook(book.getDownloadUrl());
                                        } else {
                                            Toast.makeText(getActivity(), "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(getActivity(), "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    //一级网络或者是全网络
                                    if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                                            UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                                        PublicManager.getInstance().downloadBook(book.getDownloadUrl());
                                    } else {
                                        Toast.makeText(getActivity(), "亲爱的用户，看不到我？请用手机连接互联网试一试？", Toast.LENGTH_SHORT).show();
                                    }
                                }
//                            holder.itemView.setEnabled(false);
                            }

                            Ln.d("BookcaseFragment:itemView:阅读执行结束");
                        }

                    }

                });

                holder.more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popup = new PopupMenu(getActivity(), holder.more);
                        popup.getMenuInflater()
                                .inflate(R.menu.menu_bookcase_option, popup.getMenu());
                        if(UserModule.getInstance().getRole() == Constant.USER_ROLE_PROVISIONALITY){
                            popup.getMenu().findItem(R.id.action_give_back).setVisible(false);
                            popup.getMenu().findItem(R.id.action_keep_borrow).setVisible(false);
                            popup.getMenu().findItem(R.id.action_delete).setVisible(false);
                        }else{
                            if (pgBookshelfItem.getSource() == Constant.BOOK_SOURCE_BUY) {//购买的书籍 Constant.BOOK_SOURCE_BUY
                                popup.getMenu().findItem(R.id.action_give_back).setVisible(false);
                                popup.getMenu().findItem(R.id.action_keep_borrow).setVisible(false);
                                popup.getMenu().findItem(R.id.action_provisionality_delete).setVisible(false);
                            } else if (pgBookshelfItem.getSource() == Constant.BOOK_SOURCE_BORROWING) {//借阅的书籍 Constant.BOOK_SOURCE_BORROWING
                                if(pgBookshelfItem.getType() == 1){
                                    popup.getMenu().findItem(R.id.action_delete).setVisible(false);
                                    popup.getMenu().findItem(R.id.action_provisionality_delete).setVisible(false);
                                }else {
                                    popup.getMenu().findItem(R.id.action_give_back).setVisible(false);
                                    popup.getMenu().findItem(R.id.action_keep_borrow).setVisible(false);
                                    popup.getMenu().findItem(R.id.action_delete).setVisible(false);
                                }

                            }

                        }

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                //点击归还续借还是分组
                                if (item.getItemId() == R.id.action_give_back) {//归还
                                    if (mUserInfoSecond != null) returnbook(pgBookshelfItem);

                                } else if (item.getItemId() == R.id.action_keep_borrow) {//续借
                                    long time = System.currentTimeMillis() / 1000l;
                                    long borrowTime = pgBookshelfItem.getBorrowTime();

                                    Ln.d("BookcaseFragment:itemView:续借:time:" + time);
                                    Ln.d("BookcaseFragment:itemView:续借:borrowTime:" + borrowTime);
                                    int CanBorrowTime=DatabaseManager.getInstance().queryExceedTime(mUserName);
                                    long residueTime=0;
                                    if(CanBorrowTime==-1){
                                        CanBorrowTime=30*60*60*24;
                                    }
                                    if(CanBorrowTime==0){
                                        residueTime=0;
                                    }else if(time - borrowTime < CanBorrowTime){
                                        residueTime=CanBorrowTime-time+borrowTime;
                                    }
                                    if (residueTime > 60 * 60 * 24 * 3) {
                                        Toast.makeText(mContext, "您只能在到期的前三天才能续借", Toast.LENGTH_SHORT).show();
                                    } else {
                                        renewBookById(pgBookshelfItem.getEntityId());
                                    }

                                } else if (item.getItemId() == R.id.action_delete) {//移除
                                    BookshelfManager.getInstance().removeBookById(pgBookshelfItem.getEntityId());
                                    refreshData();
                                    initBookshelfData(mGroup);
                                    File file=new File(pgBookshelfItem.getLocalUrl());
                                    deleteFile(file);
                                } else if (item.getItemId() == R.id.action_group) {//分组
                                    String[] items = new String[mSelectGroupList.size()];
                                    for (int i = 0; i < mSelectGroupList.size(); i++) {
                                        items[i] = mSelectGroupList.get(i);
                                    }

                                    showGroupDialog(items, pgBookshelfItem.getEntityId());
                                } else if (item.getItemId() == R.id.action_provisionality_delete) {//临时用户移除
                                    if (mUserInfoSecond != null) {
//                                        GDownloader.getInstance().delete(pgBookshelfItem.getDownloadUrl());
                                        GDownloader.getInstance().delete(pgBookshelfItem.getDownloadUrl());
                                        BookshelfManager.getInstance().removeBookById(pgBookshelfItem.getEntityId());
                                        File file=new File(pgBookshelfItem.getLocalUrl());
                                        deleteFile(file);
                                        initBookshelfData(mGroup);
                                        ProvisionalityDatabaseHelper pdHelper = new ProvisionalityDatabaseHelper(getActivity());
                                        pdHelper.del(pgBookshelfItem.getEntityId());
                                    }

                                }

                                return false;
                            }
                        });
                        popup.show();
                    }
                });
            }

            private void returnbook(final PgBookshelfItem pgBookshelfItem) {
                GDownloader.getInstance().delete(pgBookshelfItem.getDownloadUrl());
                UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                mToken = s;
                                Ln.d(">>>>>>>>Token:" + s);
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                                Toast.makeText(getActivity(), "归还失败", Toast.LENGTH_SHORT).show();
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                BookshelfManager.getInstance().returnBookById(
                                        mUserInfoSecond.getUserid(),
                                        mToken,
                                        pgBookshelfItem.getEntityId(),
                                        mUserInfoSecond.getOrg_id())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribeOn(Schedulers.newThread())
                                        .subscribe(new Action1<PgResult>() {
                                            @Override
                                            public void call(PgResult result) {
                                                //归还是否成功
                                                if (result.isStatus()) {
                                                    Toast.makeText(getActivity(), "归还成功", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getActivity(), "归还失败:" + result.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }, new Action1<Throwable>() {
                                            @Override
                                            public void call(Throwable throwable) {
//                                                                        android.app.AlertDialog alert = new android.app.AlertDialog.Builder(
//                                                                                getActivity()).setTitle("确认")
//                                                                                .setMessage("您的网络有问题，请确认是否归还该图书？")
//                                                                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(DialogInterface dialog,
//                                                                                                        int which) {
//                                                                                        // TODO Auto-generated method stub
//                                                                                        BookshelfManager.getInstance().removeBookById(pgBookshelfItem.getEntityId());
//                                                                                        initBookshelfData(mGroup);
//                                                                                    }
//                                                                                }).setNegativeButton("否", null).show();
//                                                                        //错误
//                                                                        Toast.makeText(getActivity(), "归还失败:" + ErrorUtils.getError(throwable), Toast.LENGTH_SHORT).show();
                                                BookshelfManager.getInstance().removeBookById(pgBookshelfItem.getEntityId());
                                                DbBookshelfEntity db = DatabaseManager.getInstance().queryBookById(pgBookshelfItem.getEntityId());
//                                                Toast.makeText(getActivity(), "dburl:" + db.getLocalUrl()+"--fileurl:"+pgBookshelfItem.getLocalUrl(), Toast.LENGTH_SHORT).show();

                                                initBookshelfData(mGroup);
                                            }
                                        }, new Action0() {
                                            @Override
                                            public void call() {
                                                //完成
                                                BookshelfManager.getInstance().removeBookById(pgBookshelfItem.getEntityId());
                                                initBookshelfData(mGroup);
                                            }
                                        });
                            }
                        });
                File file=new File(pgBookshelfItem.getLocalUrl());
                deleteFile(file);
            }

            public void deleteFile(File file) {

                if (file.exists()) { // 判断文件是否存在
                    if (file.isFile()) { // 判断是否是文件
                        file.delete(); // delete()方法 你应该知道 是删除的意思;
                    } else if (file.isDirectory()) { // 否则如果它是一个目录
                        File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                        for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
                            this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                        }
                    }
                    file.delete();
                }
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                return new ViewHolder(getActivity().getLayoutInflater().inflate(R.layout.item_bookcase, viewGroup, false));
            }

            @Override
            public int getItemCount() {
                return mList == null ? 0 : mList.size();
            }
        });
        mRecycler.setAdapter(mAdapter);
    }

    private void getReturnBook(PgBookshelfItem book){
        Toast.makeText(getActivity() , "您的借阅时间已到期，请再次借阅！" , Toast.LENGTH_SHORT).show();
        GDownloader.getInstance().delete(book.getDownloadUrl());
        //完成
        BookshelfManager.getInstance().removeBookById(book.getEntityId());
        initBookshelfData(mGroup);
        ProvisionalityDatabaseHelper pdHelper = new ProvisionalityDatabaseHelper(getActivity());
        pdHelper.del(book.getEntityId());
    }

    private void getDownloader(PgBookshelfItem pgBookshelfItem, PgBookshelfItem book){
        if (PublicManager.getInstance().isDownloadOk(book.getDownloadUrl())) {//已经下载
            final String fileUrl = PublicManager.getInstance().getFilePath(pgBookshelfItem.getDownloadUrl());
            final String readUrl = pgBookshelfItem.getLocalUrl();
            String[] uuids = UserModule.getInstance().getUUIDs();

            Ln.d("BookcaseFragment:itemView:阅读:download:" + fileUrl);
            Ln.d("BookcaseFragment:itemView:阅读:read:" + readUrl);
            Ln.d("BookcaseFragment:itemView:阅读:uuids:" + Arrays.toString(uuids));
            Log.i("阅读:download:" , fileUrl);
            Log.i("阅读:read:" , readUrl);
            Log.i("阅读:uuids:" , Arrays.toString(uuids));
//            Toast.makeText(mContext,"阅读:uuids2:" + Arrays.toString(uuids),Toast.LENGTH_SHORT).show();

            if (fileUrl != null && readUrl != null) {
                int unzipState = BookshelfManager.getInstance().getBookById(pgBookshelfItem.getEntityId()).getUnzipState();
                Ln.d("BookcaseFragment:itemView:阅读:unzipState:" + unzipState);
                if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_SUCCESS) {
                    if (fileUrl.endsWith("tdp")) {
                        if (uuids.length != 0) {

                            String succeed = BookUtils.openTdpBook(
                                    fileUrl,
                                    readUrl,
                                    uuids,
                                    mContext);
                            Ln.d("BookcaseFragment:readBook:阅读:succeed:" + succeed);

                        } else {
                            Toast.makeText(getActivity(), "用户信息出错,请重新登录", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        BookUtils.openFile(
                                fileUrl,
                                mContext);
                        Ln.d("BookcaseFragment:readBook:阅读:打开自有资源");
                    }
                } else if (unzipState == com.digital.dl2.business.util.Constant.BOOK_UNZIP_STATE_ING) {
                    Toast.makeText(getActivity(), "文件正在解压,请稍等", Toast.LENGTH_SHORT).show();
                } else {
                    Ln.d("BookcaseFragment:itemView:文件未解压");
                    Toast.makeText(getActivity(), "文件解压失败,正在重新尝试", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), MyService.class);
                    intent.putExtra(MyService.INTENT_CMD, MyService.CMD_UN_ZIP_FILE);
                    intent.putExtra(Constant.BOOK_ID, pgBookshelfItem.getEntityId());
                    mContext.startService(intent);
                }
            } else {
                Ln.d("BookcaseFragment:itemView:文件路径为空");
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "获取路径失败,请重新尝试", Toast.LENGTH_SHORT).show();
            }

        } else {
            Ln.d("BookcaseFragment:itemView:未下载下载");
            mDownloadBookId = pgBookshelfItem.getEntityId();

            //借阅的图书  如果不在二级网络下  或者是二级没登录  则不下载
            if (pgBookshelfItem.getSource() == Constant.BOOK_SOURCE_BORROWING) {
                //二级网络或者是全网络
                if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                        UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                    if (mUserInfoSecond != null) {
                        PublicManager.getInstance().downloadBook(book.getDownloadUrl());
                    } else {
                        Toast.makeText(getActivity(), "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "亲爱的用户，不对头呦！你们单位网络把我挡外面啦，领我进去嘛", Toast.LENGTH_SHORT).show();
                }
            } else {
                //一级网络或者是全网络
                if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_FIRST_LEVEL ||
                        UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                    PublicManager.getInstance().downloadBook(book.getDownloadUrl());
                } else {
                    Toast.makeText(getActivity(), "亲爱的用户，看不到我？请用手机连接互联网试一试？", Toast.LENGTH_SHORT).show();
                }
            }
        }

        Ln.d("BookcaseFragment:itemView:阅读执行结束");
    }

    private void renewBookById(final int bookId) {
        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
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
                        Toast.makeText(mContext, "续借失败", Toast.LENGTH_SHORT).show();
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        BookshelfManager.getInstance().renewBookById(
                                mUserInfoSecond.getUserid(),
                                mToken,
                                bookId,
                                mUserInfoSecond.getOrg_id())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.newThread())
                                .subscribe(new Action1<Boolean>() {
                                    @Override
                                    public void call(Boolean aBoolean) {
                                        //续借是否成功
                                        if (aBoolean) {
                                            Toast.makeText(mContext, "续借成功", Toast.LENGTH_SHORT).show();
                                            BookshelfManager.getInstance().updateBookBorrowTime(bookId);
                                        } else {
                                            Toast.makeText(mContext, "续借失败", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        //错误
                                        Toast.makeText(mContext, "续借失败:" + ErrorUtils.getError(throwable), Toast.LENGTH_SHORT).show();
                                    }
                                }, new Action0() {
                                    @Override
                                    public void call() {
                                        //完成
                                        initBookshelfData(mGroup);
                                    }
                                });
                    }
                });
    }

    class AdAdapter extends PagerAdapter {
        private List<PgAdvertisement> mList;

        @Override
        public int getCount() {
            return mList == null ? 0 : mList.size();
        }

        public void setItem(List<PgAdvertisement> list) {
            mList = list;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(getActivity());

            if (getCount() != 0) {
                final PgAdvertisement advertisement = mList.get(position);
                Ln.d("BookcaseFragment:advertisement:" + advertisement.toString());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int type = advertisement.getType();
                        if (type == 1) {//广告
                            Intent intent = new Intent(getActivity(), WebActivity.class);
                            intent.putExtra(WebActivity.INTENT_POST_URL, advertisement.getLinkUrl());
                            startActivity(intent);
                        } else {//图书
                            Intent intent = new Intent(getActivity(), CloudBookstoreBookDetailActivity.class);
                            intent.putExtra(Constant.BOOK_ID, advertisement.getBookId());
                            if (type == 2) {
                                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_BOOK);
                            } else {
                                intent.putExtra(Constant.CLOUD_BOOKSTORE_CARD_ID, Constant.CLOUD_BOOKSTORE_POD);
                            }
                            startActivity(intent);
                        }

                    }
                });
                container.addView(imageView);
                Glide.with(getActivity())
                        .load(advertisement.getImageUrl())
                        .into(imageView);
            } else {
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.drw_advertisement_default));
            }

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mContext.unregisterReceiver(mRefreshDataReceiver);
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this,null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
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
        @Bind(R.id.eye)
        ImageView eye;
        //        @Bind(R.id.time)
//        TextView time;
        @Bind(R.id.more)
        View more;
        @Bind(R.id.progress)
        BeautifulProgressbar progressbar;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }

}
