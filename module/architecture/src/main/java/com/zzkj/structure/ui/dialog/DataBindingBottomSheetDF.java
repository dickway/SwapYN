//package com.lmk.architecture.ui.dialog;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.SparseArray;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.WindowManager;
//import android.widget.FrameLayout;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.coordinatorlayout.widget.CoordinatorLayout;
//import androidx.databinding.DataBindingComponent;
//import androidx.databinding.DataBindingUtil;
//import androidx.databinding.ViewDataBinding;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.fragment.app.FragmentManager;
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.apkfuns.logutils.LogUtils;
//import com.google.android.material.bottomsheet.BottomSheetBehavior;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
//import com.lmk.architecture.ui.DataBindingConfig;
//
//public abstract class DataBindingBottomSheetDF<B extends ViewDataBinding> extends BottomSheetDialogFragment {
//    protected final String TAG = getClass().getSimpleName();
//    protected AppCompatActivity mActivity;
//    protected B mBinding;
//    protected BottomSheetBehavior<FrameLayout> behavior;
//    private BottomSheetDialog dialog;
//    private DialogInterface.OnDismissListener dismissListener;
//    private ViewModelProvider mFragmentProvider;
//    private ViewModelProvider mActivityProvider;
//
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        mActivity = (AppCompatActivity) context;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        initViewModel();
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
//        return dialog;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        DataBindingConfig dataBindingConfig = getDataBindingConfig();
//        B binding = DataBindingUtil.inflate(inflater, dataBindingConfig.getLayoutResId(),
//                container, false, getDataBindingComponent());
//        binding.setLifecycleOwner(this);
//        if (dataBindingConfig.getVmVariableId() != 0 && dataBindingConfig.getViewModel() != null) {
//            binding.setVariable(dataBindingConfig.getVmVariableId(), dataBindingConfig.getViewModel());
//        }
//        SparseArray<Object> bindingParams = dataBindingConfig.getBindingParams();
//        for (int i = 0, length = bindingParams.size(); i < length; i++) {
//            binding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
//        }
//        mBinding = binding;
//        return mBinding.getRoot();
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        FrameLayout bottomSheet = dialog.getDelegate().findViewById(com.google.android.material.R.id.design_bottom_sheet);
//        if (bottomSheet != null) {
//            bottomSheet.setBackgroundColor(Color.parseColor("#00FFFFFF"));
//            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();
//            lp.width = getWidth();
//            lp.height = getHeight();
////            lp.gravity = getGravity();
//            lp.topMargin = getTopMargin();
//            lp.bottomMargin = getBottomMargin();
//            lp.leftMargin = getStartMargin();
//            lp.rightMargin = getEndMargin();
//            bottomSheet.setLayoutParams(lp);
//        }
////        if (getDialog() != null && getDialog().getWindow() != null) {
////            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
////            WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
////            lp.width = getWidth();
////            lp.height = getHeight();
////            lp.gravity = getGravity();
////            getDialog().getWindow().setAttributes(lp);
////
//////            ViewGroup.LayoutParams lp2 = mBinding.getRoot().getLayoutParams();
//////            lp2.width = getWidth();
//////            lp2.height = getHeight();
//////            mBinding.getRoot().setLayoutParams(lp2);
////        }
//        init(savedInstanceState);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        behavior = dialog.getBehavior();
//        behavior.setState(getInitState());
//        if (disablePeekState() && getView() != null) {
//            getView().post(() -> {
//                if (getView() != null) {
//                    behavior.setPeekHeight(getView().getMeasuredHeight());
//                }
//            });
//        } else {
//            behavior.setPeekHeight(getPeekHeight());
//        }
//    }
//
//    @Override
//    public void onDismiss(@NonNull DialogInterface dialog) {
//        super.onDismiss(dialog);
//        if (dismissListener != null) {
//            dismissListener.onDismiss(dialog);
//        }
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        if (mBinding != null) {
//            mBinding.unbind();
//        }
//    }
//
//    protected abstract void init(Bundle savedInstanceState);
//
//    /**
//     * 按需重写
//     */
//    protected void initViewModel() {
//
//    }
//
//    protected abstract DataBindingConfig getDataBindingConfig();
//
//    protected DataBindingComponent getDataBindingComponent() {
//        return DataBindingUtil.getDefaultComponent();
//    }
//
//    protected int getWidth() {
//        return WindowManager.LayoutParams.MATCH_PARENT;
//    }
//
//    protected int getHeight() {
//        return WindowManager.LayoutParams.WRAP_CONTENT;
//    }
//
//    protected int getPeekHeight() {
//        return BottomSheetBehavior.PEEK_HEIGHT_AUTO;
//    }
//
//    protected int getGravity() {
//        return Gravity.CENTER;
//    }
//
//    protected int getTopMargin() {
//        return 0;
//    }
//
//    protected int getBottomMargin() {
//        return 0;
//    }
//
//    protected int getStartMargin() {
//        return 0;
//    }
//
//    protected int getEndMargin() {
//        return 0;
//    }
//
//    /**
//     * @return 初始展开状态 默认BottomSheetBehavior.STATE_EXPANDED
//     */
//    protected int getInitState() {
//        return BottomSheetBehavior.STATE_EXPANDED;
//    }
//
//    /**
//     * @return 是否禁用peek状态，禁用后下滑直接关闭,getPeekHeight()无效
//     */
//    protected boolean disablePeekState() {
//        return true;
//    }
//
//    protected <T extends ViewModel> T getFragmentScopeViewModel(@NonNull Class<T> modelClass) {
//        if (mFragmentProvider == null) {
//            mFragmentProvider = new ViewModelProvider(this);
//        }
//        return mFragmentProvider.get(modelClass);
//    }
//
//    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
//        if (mActivityProvider == null) {
//            mActivityProvider = new ViewModelProvider(mActivity);
//        }
//        return mActivityProvider.get(modelClass);
//    }
//
////    /**
////     * 用父控件的padding来设置margin，直接设置margin无法禁用peek状态
////     * 父控件为ConstraintLayout时无效，直接在布局外再套一层
////     * @param leftDpValue
////     * @param topDpValue
////     * @param rightDpValue
////     * @param bottomDpValue
////     */
////    protected void setMargin(float leftDpValue, float topDpValue, float rightDpValue, float bottomDpValue) {
////        getView().setPadding(DensityUtil.dp2px(leftDpValue),
////                DensityUtil.dp2px(topDpValue),
////                DensityUtil.dp2px(rightDpValue),
////                DensityUtil.dp2px(bottomDpValue));
////    }
//
//
//    public void setDismissListener(DialogInterface.OnDismissListener dismissListener) {
//        this.dismissListener = dismissListener;
//    }
//
//    public boolean isShowing() {
//        return getDialog() != null && getDialog().isShowing();
//    }
//
//    public void showIgnoreState(FragmentManager manager) {
//        try {
//            super.show(manager, "");
//        } catch (Exception e) {
//            LogUtils.e(e);
//        }
//    }
//
//    public void showIgnoreState(FragmentActivity activity) {
//        try {
//            super.show(activity.getSupportFragmentManager(), "");
//        } catch (Exception e) {
//            LogUtils.e(e);
//        }
//    }
//
//    public void showIgnoreState(Fragment fragment) {
//        try {
//            super.show(fragment.getChildFragmentManager(), "");
//        } catch (Exception e) {
//            LogUtils.e(e);
//        }
//    }
//}
