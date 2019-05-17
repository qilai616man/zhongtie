package com.crphdm.dl2.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brainsoon.utils.BookUtils;
import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.library.LibraryBookDetailsSecondActivity;
import com.crphdm.dl2.activity.login.LoginActivity;
import com.crphdm.dl2.activity.personal.PersonalInfoActivity;
import com.crphdm.dl2.activity.personal.PickActivity;
import com.crphdm.dl2.activity.select.SelectSearchActivity;
import com.crphdm.dl2.adapter.SearchLocalAdapter;
import com.crphdm.dl2.entity.SearchLocalKey;
import com.crphdm.dl2.fragments.bookcase.BookcaseFragment;
import com.crphdm.dl2.fragments.cloud_bookstore.MarketFragment;
import com.crphdm.dl2.fragments.library.LibraryFragment;
import com.crphdm.dl2.fragments.library.LibraryFragmentFirst;
import com.crphdm.dl2.fragments.resource.ResourceFragment;
import com.crphdm.dl2.fragments.select.SelectFragment;
import com.crphdm.dl2.user.UserModule;
import com.crphdm.dl2.user.UserModuleImpl;
import com.crphdm.dl2.user.obj.UserInfo;
import com.crphdm.dl2.user.utils.MD5;
import com.crphdm.dl2.utils.Constant;
import com.crphdm.dl2.utils.MyFbreaderClass;
import com.crphdm.dl2.utils.UpgradeHelper;
import com.crphdm.dl2.views.QrCode;
import com.digital.dl2.business.core.manager.BookshelfManager;
import com.digital.dl2.business.core.manager.PersonalCenterManager;
import com.digital.dl2.business.core.manager.PublicManager;
import com.digital.dl2.business.core.obj.PgBookForLibraryListEntity;
import com.digital.dl2.business.core.obj.PgBookshelfItem;
import com.google.gson.Gson;
import com.goyourfly.gdownloader.utils.Ln;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.geometerplus.android.MyInterface.SearchLocalCountInterface;
import org.geometerplus.android.fbreader.libraryService.BookCollectionShadow;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements LibraryFragment.OnInstitutionLibraryListFragmentListener,LibraryFragmentFirst.OnInstitutionLibraryListFragmentListener {
    private final static int SCANNIN_GREQUEST_CODE = 1;
    private static final int FILE_SELECT_CODE = 2;

    private static final int ACCESS_COARSE_LOCATION_REQUEST_CODE = 1;
    private static final int READ_PHONE_STATE_REQUEST_CODE = 2;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 3;

    SectionsPagerAdapter mSectionsPagerAdapter;
    @Bind(R.id.viewPager)
    ViewPager mViewPager;
    @Bind(R.id.tabLayout)
    TabLayout mTabLayout;
    @Bind(R.id.profile_image)
    CircleImageView mProfileImage;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.search)
    ImageView search;

    @Bind(R.id.select_pick_detail)
    ImageView selectPickDetail;

    //search_local_key 中的控件
    @Bind(R.id.search_local_back)
    ImageView search_local_back;
    @Bind(R.id.search_local_search)
    SearchView search_local_search;
    @Bind(R.id.search_local_recycler)
    RecyclerView serch_local_recycler;
    @Bind(R.id.search_local_relative)
    RelativeLayout search_local_relative;

    private UserInfo mUserInfoFirst;
    private UserInfo mUserInfoSecond;
    private String mTokenFirst;
    private String mTokenSecond;

    private int mLibraryBookType;

    private boolean isAccessCoarseLocation = false;
    private boolean isReadPhoneState = false;
    private boolean isReadExternalStorage = false;

    @OnClick(R.id.profile_image)
    public void onIconClick() {
        Intent intent = new Intent(this, PersonalInfoActivity.class);
        startActivity(intent);

    }
    @OnClick(R.id.title)
    public void ontitleClick() {
//        final String fileUrl = Environment.getExternalStorageDirectory() + "/crphdm/download/kmljjg_2_mobi.tdp";//t200100005_1358_mobi.tdp
//        final String readUrl = Environment.getExternalStorageDirectory() + "/crphdm/read/kmljjg_2_mobi/";//1358/
//        //de4d3618d2c7b5dcba77cf7a2528d11c144c98da4719a997ca3e1220a7fe8e6fc3bdb0a35b3f29801c4ae939c96a8f85
//        String[] uuids = new String[]{"1ff26b5e61ca0a5b20a3786b753aa10ceb18cdfe1d66ecd7016227f80ca46433c3bdb0a35b3f29801c4ae939c96a8f85"};
//        String code = "7b9ea4bf349ccd7b358318b570bbd3adf2d98ff109a2ad0d6f835fa4e5e1e471286945d2094150121629ec76c3c51b63389faed42b4c9468fabec391b914928a446c76adecc3644f7f1eda066447c99e72bccbb58c3a69f1c3de61da5859a2870bb37376a95d48f5e19665db026a611d2434790ec087ec6de74cf553de4208bef743207c67d1178bba9236b574c6e9691ff3e49c14b5f7b778c6bb5c5aebb4af5fba9fe0661158c02f318a9da9d739c0da485c262f2450a7fe74e2979f7606a09406cf74bf515d7760eb2c0b342ee9e745d24291f9bf53672d7460f57aff736564c2c7446abde4c3f163187062019066ec870b7774794253442dd19b36ebcea0eca6ab693d02f4e325982ace53f21f3d2c72dccd7b61135425537a8d87b06d9e1a15bbb0cbff7f2198647d28499778bdec870b7774794253442dd19b36ebcea0eca6ab693d02f4e325982ace53f21f3dd42fbd308c839e2f76eadda3cfaa4d9aa53214f260fc28c7e6f4fa827146a37c22fbc90a3c1109a6af8e568115f414aca53214f260fc28c7e6f4fa827146a37cd04a85848490615e712eecd3a1d9b7bfe362e0736651f59ded0e6642a4312aeca53214f260fc28c7e6f4fa827146a37c62956130d10eb20d36af88ca7bdebcdb5fac716e43d7a00f54d58256968180e4f198dfb0cdf46d3d7bb640fc6d974c25e39659e520465fe97889460be8bdf192dff250739534b5b3566c6defcaae2e515da4969036a1cfe73f168400503c14fc";
//        BookUtils.bkjgg("","");
//        BookUtils.openTdpBookCode(
//                fileUrl,
//                readUrl,
//                uuids,
//                code,
//                this);
    }
    @OnClick(R.id.select_pick_detail)
    public void onSelectPickDetail(){
        Intent intent = new Intent(this , PickActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.search)
    public void onSearchClick() {
//        if(mViewPager.getCurrentItem()==0){
//            search_local_relative.setVisibility(View.VISIBLE);
//            if(search_local_search.isIconified()){
//                search_local_search.setIconified(false);
//            }
//        }else
            if(mViewPager.getCurrentItem() == 4){
            Intent intent = new Intent(this , SelectSearchActivity.class);
            if(UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                    UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_UNKNOWN){
                Toast.makeText(MainActivity.this , "亲爱的用户,看不到我?请用手机连接互联网试一试?" , Toast.LENGTH_SHORT).show();
                return ;
            }else {
                intent.putExtra(SelectSearchActivity.INTENT_FROM, SelectSearchActivity.FROM_STORE);
            }
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, SearchActivity.class);
            if (mViewPager.getCurrentItem() == 2) {//云书城

                if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                        UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_UNKNOWN) {
                    Toast.makeText(MainActivity.this, "亲爱的用户,看不到我?请用手机连接互联网试一试?", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    intent.putExtra(SearchActivity.INTENT_FROM, SearchActivity.FROM_STORE);
                }

            } else if (mViewPager.getCurrentItem() == 3) {//图书馆

                if (UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND) != null &&
                        UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND).getUserid() != 0) {

                    intent.putExtra(SearchActivity.INTENT_FROM, SearchActivity.FROM_LIBRARY);
                    intent.putExtra(SearchActivity.INTENT_LIBRARY_TYPE, mLibraryBookType);

                } else {
                    showDialog("您不是本机构用户，请联系管理员");
                    return;
                }
            }

            startActivity(intent);
        }

    }
    //通过eventbus获取书架中图书信息
    //gy 0921
    private List<PgBookshelfItem> pgBookshelfItems;
    private List<SearchLocalKey> keyItemList=new ArrayList<>();
    public void onEventMainThread(List<PgBookshelfItem> mList) {

        pgBookshelfItems=mList;
    }

   public void refreshSearchCount(String query){
        keyItemList.clear();
       if (pgBookshelfItems.size()>0){
           for(PgBookshelfItem item:pgBookshelfItems){
               String name = item.getName();

               String myurl = PublicManager.getInstance().getFilePath(item.getDownloadUrl());
               String myreadUrl = item.getLocalUrl();
               String[] myuuids = UserModule.getInstance().getUUIDs();
               String myasskey = BookUtils.getContentKey(myurl,myreadUrl,myuuids,this);
               String count = MyFbreaderClass.makeFbreader(myasskey,myurl,bookCollectionShadow,query);

               SearchLocalKey searchLocalKey = new SearchLocalKey();
               searchLocalKey.setCount(count);
               searchLocalKey.setName(name);
               searchLocalKey.setAssKey(myuuids);
               searchLocalKey.setLocalUrl(myreadUrl);
               searchLocalKey.setFileUrl(myurl);
//               final String fileUrl = PublicManager.getInstance().getFilePath(pgBookshelfItem.getDownloadUrl());
//               final String readUrl = pgBookshelfItem.getLocalUrl();

               keyItemList.add(searchLocalKey);
           }
       }
       Collections.sort(keyItemList, new Comparator<SearchLocalKey>() {
           @Override
           public int compare(SearchLocalKey lsearchLocalKey, SearchLocalKey rsearchLocalKey) {

               return Integer.parseInt(rsearchLocalKey.getCount())-Integer.parseInt(lsearchLocalKey.getCount());
           }
       });
   }


    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    private int roleId;
    private boolean isExhibition;

    BookCollectionShadow bookCollectionShadow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        roleId = UserModule.getInstance().getRole();
        if (UserModuleImpl.getInstance().isLogin()) {
            UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            Ln.d("MainActivity:GetTokenAsync:" + s);
                        }
                    });

        } else {

            refreshNetState();

        }
        initViewPager();
        initMembers();
        initPermissions();
        upgrade();
        bookCollectionShadow= new BookCollectionShadow();
        EventBus.getDefault().register(this);
    }



    private Integer mNetState;
    private void refreshNetState() {
        UserModuleImpl.getInstance().getNetState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mNetState = integer;
                        Ln.d("WelcomeActivity:getNetState:integer:" + integer);

                        UserModule.getInstance().setNetStateLocal(integer);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Ln.d("WelcomeActivity:getNetState:error");

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Ln.d("WelcomeActivity:getNetState:ok");
                        if (mNetState == UserModule.NET_STATE_UNKNOWN) {
                            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent2);
                        } else if (mNetState == UserModule.NET_STATE_FIRST_LEVEL) {
                            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent2);
                        } else if (mNetState == UserModule.NET_STATE_SECOND_LEVEL) {
                            getNetExhibition();
                        } else if (mNetState == UserModule.NET_STATE_ALL) {
                            getNetExhibition();
                        } else {
                            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent2);
                        }
                    }
                });
    }
    private void getNetExhibition(){
        UserModuleImpl.getInstance().getExhibition()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        isExhibition = aBoolean;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent2);
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if(isExhibition){
                            final String username = "t200100001";
//                            final String username = "t232100001";
                            String password = "123456";

                            final String md5Password = new MD5().md5(password);
                            UserModule.getInstance().login(username, md5Password, UserModule.USER_TYPE_EMAIL).subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<UserInfo>() {
                                        @Override
                                        public void call(UserInfo userInfo) {

                                        }
                                    }, new Action1<Throwable>() {
                                        @Override
                                        public void call(Throwable throwable) {
                                            String throwableString = String.valueOf(throwable);
                                            Ln.d("LoginActivity:UserInfo:throwableString:" + throwableString);
                                            if (throwableString.contains("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was STRING at line 1 column 1 path $")) {
                                                UserModule.getInstance().loginOld(username, md5Password, UserModule.USER_TYPE_EMAIL)
                                                        .subscribeOn(Schedulers.newThread())
                                                        .observeOn(AndroidSchedulers.mainThread())
                                                        .subscribe(new Action1<UserInfo>() {
                                                            @Override
                                                            public void call(UserInfo userInfo) {

                                                            }
                                                        });

                                            }
                                        }
                                    });
                        }else {
                            Intent intent2 = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent2);
                        }
                    }
                });

    }

    private void upgrade() {
        Ln.d("MainActivity:upgrade");
        UpgradeHelper.checkUpgradeByWifi(
                MainActivity.this,
                true,
                com.jiuwei.upgrade_package_new.lib.Constant.DIALOG_STYLE_ELDERLY_ASSISTANT);
    }

    private void initMembers() {
        mUserInfoFirst = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        mUserInfoSecond = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_SECOND);

        setUpLogo();

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_FIRST)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mTokenFirst = s;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        initDbDataByBuy();
                    }
                });

        UserModule.getInstance().getTokenAsync(UserModule.NET_CENTER_SECOND)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        mTokenSecond = s;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        if(roleId != Constant.USER_ROLE_PROVISIONALITY){
                            initDBDataByBorrowing();
                        }

                    }
                });
    }

    private void initDbDataByBuy() {
        if (mUserInfoFirst != null) {
            PersonalCenterManager.getInstance().getMyBuyList(mUserInfoFirst.getUserid(), mTokenFirst, 0, 100)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<PgBookForLibraryListEntity>>() {
                        @Override
                        public void call(List<PgBookForLibraryListEntity> list) {
                            BookshelfManager.getInstance()
                                    .insertBookList(PgBookshelfItem.getPgListByLibraryBookList(list));

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    }, new Action0() {
                        @Override
                        public void call() {

                        }
                    });
        }
    }

    private void initDBDataByBorrowing() {
        if (mUserInfoSecond != null) {
            BookshelfManager.getInstance().getBorrowingBookList(mUserInfoSecond.getUserid(), mTokenSecond)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<List<PgBookshelfItem>>() {
                        @Override
                        public void call(List<PgBookshelfItem> pgBookshelfItems) {
                            BookshelfManager.getInstance().insertBookList(pgBookshelfItems);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {

                        }
                    }, new Action0() {
                        @Override
                        public void call() {

                        }
                    });
        }
    }

    public void setUpLogo() {
        if (mUserInfoFirst != null && mUserInfoFirst.getPhoto() != null && !mUserInfoFirst.getPhoto().equals("")) {
            mProfileImage.setImageResource(R.drawable.drw_1_touxiang_new);
            ImageLoader.getInstance().displayImage(mUserInfoFirst.getPhoto(), mProfileImage);
        } else {
            mProfileImage.setImageResource(R.drawable.drw_1_touxiang_new);
        }
    }

    private void initViewPager() {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.layout_home_tab_bookrack));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.layout_home_tab_resource));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.layout_home_tab_store));
        mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.layout_home_tab_library));
        if (roleId == Constant.USER_ROLE_CAIXUANYUAN || roleId == Constant.USER_ROLE_LINGDAO) {//采选员 ，领导
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.layout_home_tab_select));

        }
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        search.setVisibility(View.GONE);
        selectPickDetail.setVisibility(View.GONE);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mTitle.setText(mSectionsPagerAdapter.getPageTitle(position));
                if (position == 0 || position == 1) {//书架和资源
                    search.setVisibility(View.GONE);
                    selectPickDetail.setVisibility(View.GONE);
                } else if (position == 3) {//图书馆
                    selectPickDetail.setVisibility(View.GONE);
                    if (UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_SECOND_LEVEL ||
                            UserModule.getInstance().getNetStateLocal() == Constant.NET_STATE_ALL) {
                        search.setVisibility(View.VISIBLE);
                    } else {
                        search.setVisibility(View.GONE);
                    }
                } else {
                    if(position == 4 && roleId == Constant.USER_ROLE_CAIXUANYUAN){
                        selectPickDetail.setVisibility(View.VISIBLE);
                    }else{
                        selectPickDetail.setVisibility(View.GONE);
                    }
                    search.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_REQUEST_CODE);
        } else {
            Ln.d("已有位置信息权限");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    READ_PHONE_STATE_REQUEST_CODE);
        } else {
            Ln.d("已有读取手机状态权限");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            Ln.d("已有存储空间权限");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String result = bundle.getString("result");
                    Ln.d("MainActivity:onActivityResult:result:" + result);

//                    Toast.makeText(MainActivity.this, "扫描二维码：" + result, Toast.LENGTH_LONG).show();

                    if (result != null && result.length() > 4 && result.substring(0, 4).equals("http")) {
                        Ln.d("MainActivity:onActivityResult:result:url");
                        return;
                    }

                    QrCode qrCode = new Gson().fromJson(result, QrCode.class);

                    if (qrCode != null) {

                        int bookId = qrCode.getBookid();
                        int type = qrCode.getType();

                        Ln.d("MainActivity:onActivityResult:bookId:" + bookId);
                        Ln.d("MainActivity:onActivityResult:type:" + type);

                        if (bookId != 0) {
                            Intent intent = new Intent(MainActivity.this, LibraryBookDetailsSecondActivity.class);
                            intent.putExtra(Constant.BOOK_ID, bookId);
                            intent.putExtra(Constant.LIBRARY_DETAIL_TYPE, type);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "未找到此图书", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "未找到此图书", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {

                }

                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == ACCESS_COARSE_LOCATION_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Ln.d("允许位置信息权限");
                isAccessCoarseLocation = true;
            }else {
                Ln.d("拒绝位置信息权限");
                isAccessCoarseLocation = false;
            }
        }else if(requestCode == READ_PHONE_STATE_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Ln.d("允许读取手机状态权限");
                isReadPhoneState = true;
            }else {
                Ln.d("拒绝读取手机状态权限");
                isReadPhoneState = false;
            }
        }else if(requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Ln.d("允许存储空间权限");
                isReadExternalStorage = true;
            }else {
                Ln.d("拒绝存储空间权限");
                isReadExternalStorage = false;
            }
        }
    }

    /**
     * 图书馆  type
     *
     * @param i 1.电子书  2.自有资源  3.共享资源
     */
    @Override
    public void onClickInstitutionLibraryList(int i) {
        mLibraryBookType = i;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Ln.d("GetItem>>>" + position);
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            if(roleId == Constant.USER_ROLE_CAIXUANYUAN || roleId == Constant.USER_ROLE_LINGDAO){
                return 5;
            }else{
                return 4 ;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(roleId == Constant.USER_ROLE_CAIXUANYUAN || roleId == Constant.USER_ROLE_LINGDAO){
                switch (position) {
                    case 0:
                        return "书架";
                    case 1:
                        return "共享资源";
                    case 2:
                        return "云书城";
                    case 3:
                        return "机构图书馆";
                    case 4 :
                        return "资源采选";
                }
            }else{
                switch (position) {
                    case 0:
                        return "书架";
                    case 1:
                        return "共享资源";
                    case 2:
                        return "云书城";
                    case 3:
                        return "机构图书馆";
                }
            }

            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int mSectionNumber;
        private ProgressDialog mProgressDialog;
        private int mNetState = Constant.NET_STATE_UNKNOWN;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mSectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            }

            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(getActivity(), null, "加载中...");
            }

        }

        @Override
        public void onResume() {
            super.onResume();



        }

        private void initFragment() {
            FragmentManager fm = getChildFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack(null);

            if (mSectionNumber == 0) {
                //不用判断网络
                ft.replace(R.id.fl_1_layout, BookcaseFragment.newInstance(mNetState)).commitAllowingStateLoss();

            } else if (mSectionNumber == 1) {
                //只有一级
                //如果是二级   显示无网络
                ft.replace(R.id.fl_1_layout, ResourceFragment.newInstance(mNetState)).commitAllowingStateLoss();

            } else if (mSectionNumber == 2) {
                //只有一级
                //如果是二级   显示无网络

                ft.replace(R.id.fl_1_layout, MarketFragment.newInstance(mNetState)).commitAllowingStateLoss();

            } else if (mSectionNumber == 3) {
//                ft.replace( R.id.fl_1_layout, LibraryFragment.newInstance(mNetState)).commitAllowingStateLoss();
               // 优先显示二级
                Ln.d("NetState:" + mNetState);
                if (mNetState == Constant.NET_STATE_ALL || mNetState == Constant.NET_STATE_SECOND_LEVEL) {
                    Ln.d("LibraryFragment:show");
                    ft.replace( R.id.fl_1_layout, LibraryFragment.newInstance(mNetState)).commitAllowingStateLoss();
                } else if (mNetState == Constant.NET_STATE_FIRST_LEVEL) {
                    Ln.d("InstitutionLibraryListFragment:show");
//                    ft.replace(R.id.fl_1_layout, InstitutionLibraryListFragment.newInstance(mNetState)).commitAllowingStateLoss();
                    ft.replace( R.id.fl_1_layout, LibraryFragmentFirst.newInstance(mNetState)).commitAllowingStateLoss();
                }
            } else if (mSectionNumber == 4){
                ft.replace(R.id.fl_1_layout, SelectFragment.newInstance(mNetState)).commitAllowingStateLoss();
            }
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            UserModuleImpl.getInstance().getNetState()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Action1<Integer>() {
                        @Override
                        public void call(Integer integer) {
                            mNetState = integer;
                            Ln.d("MainActivity:Fragment:mNetState:" + mNetState);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Ln.d("MainActivity:Fragment:error");
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            initFragment();
                        }
                    }, new Action0() {
                        @Override
                        public void call() {
                            Ln.d("MainActivity:Fragment:ok");
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                                mProgressDialog = null;
                            }
                            initFragment();
                        }
                    });
            return rootView;
        }
    }


    private int mBackKeyClickedNum = 0;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onBackPressed() {
        mBackKeyClickedNum++;

        if(search_local_relative.isShown()){
            search_local_relative.setVisibility(View.GONE);
            if (keyItemList.size()>0){
                keyItemList.clear();
            }
        }


        if (mBackKeyClickedNum > 1) {
            moveTaskToBack(true);
            return;
        }

        Toast.makeText(getApplicationContext(), "再点一次退出", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackKeyClickedNum = 0;
            }
        }, 1000);
    }

    SearchLocalAdapter searchLocalAdapter;
    @Override
    protected void onResume() {
        super.onResume();

        mUserInfoFirst = UserModule.getInstance().getUserInfoLocal(UserModule.NET_CENTER_FIRST);
        setUpLogo();
        MobclickAgent.onResume(this);

        if(!isAccessCoarseLocation || !isReadPhoneState || !isReadExternalStorage){
            initPermissions();
        }

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        serch_local_recycler.setLayoutManager(linearLayoutManager);
        searchLocalAdapter=new SearchLocalAdapter(this);
        serch_local_recycler.setAdapter(searchLocalAdapter);

        search_local_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                bookCollectionShadow.MyBindToService(MainActivity.this, new SearchLocalCountInterface() {
                    @Override
                    public void resfreshCount() {

                        refreshSearchCount(query);
                        Log.i("ccccccc","size=="+keyItemList.size());
                        if(keyItemList.size()>0){
                            searchLocalAdapter.setData(keyItemList);
                            searchLocalAdapter.notifyDataSetChanged();
                        }
                        bookCollectionShadow.unbind();
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                 if(keyItemList.size()>0){
                     keyItemList.clear();
                     searchLocalAdapter.notifyDataSetChanged();
                 }
                return false;
            }
        });

        search_local_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_local_relative.setVisibility(View.GONE);
                keyItemList.clear();
            }
        });
        if(keyItemList.size()>0){
            searchLocalAdapter.setData(keyItemList);
            searchLocalAdapter.notifyDataSetChanged();
        }
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);

    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this,null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
}
