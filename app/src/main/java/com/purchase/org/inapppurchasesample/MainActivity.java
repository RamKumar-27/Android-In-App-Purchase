package com.purchase.org.inapppurchasesample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.purchase.org.inapppurchasesample.otherUtils.IabHelper;
import com.purchase.org.inapppurchasesample.otherUtils.IabResult;
import com.purchase.org.inapppurchasesample.otherUtils.Inventory;
import com.purchase.org.inapppurchasesample.otherUtils.Purchase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener {


    static final String ITEM_SKU = "com.purchase.org.inapppurchasesample.purchase";
    @BindView(R.id.button)
    Button purchase;
    @BindView(R.id.button2)
    TextView button2;
    private String encodedPublicKey = "<Private key>";
    private IabHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        helper = new IabHelper(this, encodedPublicKey);
        helper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("PURCHASE", "In-app Billing setup failed: " +
                            result);
                } else {
                    Log.d("PURCHASE", "In-app Billing is set up OK");
                }
            }
        });
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        try {
            helper.launchPurchaseFlow(this, ITEM_SKU, 15676, this, "purchaseToken");
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase info) {
        if (result.isFailure()) {
            Log.e("FINISH HERROR", result.getMessage());
            return;
        } else if (info.getSku().equals(ITEM_SKU)) {
            consumeItem();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!helper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void consumeItem() {
        try {
            helper.queryInventoryAsync(this);
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        if (result.isFailure()) {
            Log.e("Failure", result.getMessage());
        } else {
            try {
                helper.consumeAsync(inv.getPurchase(ITEM_SKU),
                        new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                if (result.isSuccess()) {
                                    button2.setText("success");
                                } else {
                                    // handle error
                                }
                            }
                        });
            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
        }
    }
}
