package com.crphdm.dl2.fragments.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.crphdm.dl2.R;
import com.crphdm.dl2.activity.library.MoreFragmentActivity;
import com.crphdm.dl2.activity.library.UploadSelfResourceActivity;
import com.goyourfly.gdownloader.utils.Ln;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LibraryFragmentFirst extends Fragment {
    //图书馆首页
    @Bind(R.id.ll_more_library)
    LinearLayout llMoreLibrary;
    @Bind(R.id.ll_scan_code)
    LinearLayout llScanCode;
    @Bind(R.id.ll_upload_data)
    LinearLayout llUploadData;
    @Bind(R.id.tb_institution_library)
    TabLayout tbInstitutionLibrary;
    @Bind(R.id.frame_layout)
    FrameLayout frameLayout;

    @Bind(R.id.ll_select_data)
    LinearLayout llSelectData;

    private static final String ARG_NET_STATE = "ARG_NET_STATE";
    private final static int SCANNING_QR_CODE = 1;
    private LibraryElectronicBookFragmentFirst mLibraryElectronicBookFragment;

    private int mNetState;
    //初始化Fragment。可通过参数savedInstanceState获取之前保存的值
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNetState = getArguments().getInt(ARG_NET_STATE);
            Ln.d("LibraryFragment:mNetState:" + mNetState);
        }
    }

    public static LibraryFragmentFirst newInstance(int netState) {
        LibraryFragmentFirst fragment = new LibraryFragmentFirst();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_NET_STATE, netState);
        fragment.setArguments(bundle);
        return fragment;
    }
    //参数savedInstanceState获取之前保存的值
    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
    }
    //初始化Fragment的布局。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_library, container, false);

        ButterKnife.bind(this, view);
        return view;
    }
    //执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        if (UserModule.getInstance().getRole() == Constant.USER_ROLE_CAIXUANYUAN) {//采选员
            llSelectData.setVisibility(View.GONE);
//        }else {
//            llSelectData.setVisibility(View.GONE);
//        }
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("电子书"));
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("自有资源"));
        tbInstitutionLibrary.addTab(tbInstitutionLibrary.newTab().setText("共享资源"));

        showElectronicBookFragment();

        tbInstitutionLibrary.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    showElectronicBookFragment();
                } else if (tab.getPosition() == 1) {
                    showSelfResourceFragment();
                } else if (tab.getPosition() == 2) {
                    showShareResourceFragment();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        /**
         * 更多图书馆
         */
        llMoreLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // showInstitutionLibraryListFragment();
//                mListener.onClickInstitutionLibraryList();
                startActivity(new Intent(getActivity(), MoreFragmentActivity.class));
            }
        });

        /**
         * 上传资料
         */
        llUploadData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), UploadSelfResourceActivity.class));
            }
        });

    }

    /**
     * 扫描二维码
     */
    @OnClick(R.id.ll_scan_code)
    public void scanCode() {

        showDialog("亲爱的用户，请先连接自助阅读机的wifi后再进行扫码下载图书！");

    }

    /**
     * 显示电子书
     */
    private void showElectronicBookFragment() {
        if (mLibraryElectronicBookFragment == null) {
            mLibraryElectronicBookFragment = new LibraryElectronicBookFragmentFirst();
        }

        mListener.onClickInstitutionLibraryList(1);

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, mLibraryElectronicBookFragment).commit();
    }

    /**
     * 显示自有资源
     */
    private void showSelfResourceFragment() {

        mListener.onClickInstitutionLibraryList(2);

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, LibraryResourceListFragment.newInstance(LibraryResourceListFragment.TYPE_SELF)).commit();
    }

    /**
     * 显示共享资源
     */
    private void showShareResourceFragment() {
        mListener.onClickInstitutionLibraryList(3);

        getChildFragmentManager().beginTransaction().replace(
                R.id.frame_layout, LibraryResourceListFragment.newInstance(LibraryResourceListFragment.TYPE_SHARE)).commit();
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
    //执行该方法时，Fragment处于活动状态，用户可与之交互。
    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("图书馆首页");
    }
    //执行该方法时，Fragment处于暂停状态，但依然可见，用户不能与之交互。
    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("图书馆首页");
    }

   //解除与Activity的绑定。在onDestroy方法之后调用
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

    }

    private OnInstitutionLibraryListFragmentListener mListener;
    //执行该方法时，Fragment与Activity已经完成绑定，该方法有一个Activity类型的参数，代表绑定的Activity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnInstitutionLibraryListFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnInstitutionLibraryListFragmentListener {
        void onClickInstitutionLibraryList(int i);
    }
    //销毁与Fragment有关的视图，但未与Activity解除绑定
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this , null);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
