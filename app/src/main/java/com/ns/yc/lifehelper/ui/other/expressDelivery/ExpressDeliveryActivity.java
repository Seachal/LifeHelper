package com.ns.yc.lifehelper.ui.other.expressDelivery;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.ns.yc.lifehelper.R;
import com.ns.yc.lifehelper.base.mvp.BaseActivity;
import com.ns.yc.lifehelper.ui.other.expressDelivery.activity.ExpressDeliveryInfoActivity;
import com.ns.yc.lifehelper.ui.other.expressDelivery.indexModel.SelectorCompanyActivity;

import butterknife.BindView;

/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2017/8/18
 * 描    述：快递查询页面
 * 修订历史：
 * ================================================
 */
public class ExpressDeliveryActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ll_title_menu)
    FrameLayout llTitleMenu;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.et_number)
    EditText etNumber;
    @BindView(R.id.tv_company)
    TextView tvCompany;
    @BindView(R.id.tv_search)
    TextView tvSearch;
    private String type;
    private String number;

    @Override
    public int getContentView() {
        return R.layout.activity_express_delivery;
    }

    @Override
    public void initView() {
        toolbarTitle.setText("选择公司");
    }

    @Override
    public void initListener() {
        tvCompany.setOnClickListener(this);
        llTitleMenu.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_company:
                number = etNumber.getText().toString().trim();
                Intent intent = new Intent(this, SelectorCompanyActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.ll_title_menu:
                finish();
                break;
            case R.id.tv_search:
                startSearch();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100 && data!=null){
            String name = data.getStringExtra("name");
            type = data.getStringExtra("type");
            tvCompany.setText(name);
            etNumber.setText(number);
        }
    }

    private void startSearch() {
        String number = etNumber.getText().toString().trim();
        String company = tvCompany.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            ToastUtils.showShort("快递号不能为空");
            return;
        }
        if(TextUtils.isEmpty(company)){
            ToastUtils.showShort("快递公司不能为空");
            return;
        }

        Intent intent = new Intent(ExpressDeliveryActivity.this,ExpressDeliveryInfoActivity.class);
        intent.putExtra("number",number);
        intent.putExtra("type",type);
        startActivity(intent);
    }



}
