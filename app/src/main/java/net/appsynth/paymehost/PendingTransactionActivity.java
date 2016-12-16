package net.appsynth.paymehost;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import net.appsynth.android.hsbcpaymewheel.models.HGTransaction;
import net.appsynth.android.hsbcpaymewheel.sdk.HSBCGame;
import net.appsynth.android.hsbcpaymewheel.sdk.errors.HGError;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGDoubleCheckTransactionInterface;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGRequestPendingInterface;
import net.appsynth.android.hsbcpaymewheel.sdk.interfaces.HGSuccessInterface;
import net.appsynth.android.hsbcpaymewheel.utils.DialogUtils;

import java.util.List;

public class PendingTransactionActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<HGTransaction> mPendingTransaction;
    private PendingTransactionAdapter mAdapter;
    private LinearLayout mResultLayout;
    private ScrollView mResultScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_transaction);
        setTitle("Pending Transaction");
        mRecyclerView = (RecyclerView)findViewById(R.id.pending_main_recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mResultLayout = (LinearLayout) findViewById(R.id.pending_result_layout);
        mResultScrollView = (ScrollView) findViewById(R.id.pending_result_scrollview);

        HSBCGame.getPendingTransactions(new HGRequestPendingInterface() {
            @Override
            public void onSuccess(List<HGTransaction> pendingTransaction) {
                mPendingTransaction = pendingTransaction;
                mAdapter = new PendingTransactionAdapter();
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFail(HGError error) {
                DialogUtils.showOKialog(PendingTransactionActivity.this, "getTransactionsPending onFail: "+error.getErrorCode());
            }
        });

    }

    private void showAlertDialogChoices(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.label_choose_action)
                .setItems(R.array.pending_choices, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //reject
                        if (which == 0) {
                            rejectTransaction(position);
                        }
                        //confirm
                        else if (which == 1) {
                            confirmTransaction(position);
                        }
                        //double check
                        else if (which == 2) {
                            doubleCheckTransaction(position);
                        }
                    }
                });
        builder.create().show();
    }

    private void rejectTransaction(final int position) {
        HGTransaction transaction = mPendingTransaction.get(position);
        HSBCGame.rejectTransaction(transaction.getTransactionId(), new HGSuccessInterface() {
            @Override
            public void onSuccess() {
                mPendingTransaction.remove(position);
                mAdapter.notifyDataSetChanged();
                addItem("Reject Transaction: Success");
            }

            @Override
            public void onFail(HGError error) {
                addItem("Reject Transaction: Error");
            }
        });
    }

    private void confirmTransaction(final int position) {
        HGTransaction transaction = mPendingTransaction.get(position);
        HSBCGame.confirmTransaction(transaction.getTransactionId(), new HGSuccessInterface() {
            @Override
            public void onSuccess() {
                mPendingTransaction.remove(position);
                mAdapter.notifyDataSetChanged();
                addItem("Confirm Transaction : Success");
            }

            @Override
            public void onFail(HGError error) {
                addItem("Confirm Transaction : Error");
            }
        });
    }

    private void doubleCheckTransaction(final int position) {
        HGTransaction transaction = mPendingTransaction.get(position);
        HSBCGame.doubleCheckAmount(transaction.getTransactionId(), new HGDoubleCheckTransactionInterface() {
            @Override
            public void onSuccess(HGTransaction transaction) {
                addItem(transaction);
            }

            @Override
            public void onFail(HGError error) {
                DialogUtils.showOKialog(PendingTransactionActivity.this, "Error doubleCheck :" + error.getErrorCode());
                addItem("Double Check Amount : Error");
            }
        });
    }

    boolean isFirstTimeShowResult = true;

    private void addItem(String text) {

        if (isFirstTimeShowResult) {
            isFirstTimeShowResult = false;
            mResultScrollView.setVisibility(View.VISIBLE);
        }

        TextView mTextview = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextview.setPadding(pxFromDp(10), pxFromDp(10), pxFromDp(10), 0);
        mTextview.setLayoutParams(params);
        mTextview.setText(text);
        mTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTextview.setTextColor(Color.WHITE);
        mResultLayout.addView(mTextview);
    }


    private void addItem(HGTransaction transaction) {
        addItem("Double Check Amount");

        TextView mTransactionTextview = new TextView(this);
        mTransactionTextview.setPadding(pxFromDp(10), 0, pxFromDp(10), 0);
        mTransactionTextview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mTransactionTextview.setText("Transaction Id: " + transaction.getTransactionId());
        mTransactionTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTransactionTextview.setTextColor(Color.WHITE);

        TextView mAmountTextview = new TextView(this);
        mAmountTextview.setPadding(pxFromDp(10), 0, pxFromDp(10), pxFromDp(10));
        mAmountTextview.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mAmountTextview.setText("Amount: " + String.format("$%.2f",transaction.getAmount()/100.0f));
        mAmountTextview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mAmountTextview.setTextColor(Color.WHITE);

        mResultLayout.addView(mTransactionTextview);
        mResultLayout.addView(mAmountTextview);
    }


    public int pxFromDp(final float dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return (int)px;
    }

    class PendingTransactionAdapter extends RecyclerView.Adapter<ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PendingTransactionActivity.this)
                    .inflate(R.layout.item_pending, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
            HGTransaction transaction = mPendingTransaction.get(position);
            holder.mTitleText.setText(transaction.getTransactionId());
            holder.mDescriptionTextView.setText(String.format("$%.2f",transaction.getAmount()/100.0f));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlertDialogChoices(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPendingTransaction.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView mTitleText;
        TextView mDescriptionTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitleText = (TextView)itemView.findViewById(R.id.pending_item_title_textView);
            mDescriptionTextView = (TextView)itemView.findViewById(R.id.pending_item_description_textView);
        }

    }

    public static void openActivity(Context context) {
        Intent intent = new Intent(context, PendingTransactionActivity.class);
        context.startActivity(intent);
    }
}
