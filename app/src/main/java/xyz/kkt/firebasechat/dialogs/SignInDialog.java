package xyz.kkt.firebasechat.dialogs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.aungpyaephyo.mmtextview.components.MMTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xyz.kkt.firebasechat.R;

public class SignInDialog extends DialogFragment {
    //implements View.OnClickListener {

    @BindView(R.id.btn_agree)
    MMTextView btnAgree;

    @BindView(R.id.btn_disgree)
    MMTextView btnDisgree;

    private TapListener tapListener;

    public interface TapListener {
        void onTapAgree();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_sign_in, container, false);
        ButterKnife.bind(this, view);

//        btnAgree.setOnClickListener(this);
//        btnDisgree.setOnClickListener(this);

        return view;
    }


    @OnClick(R.id.btn_agree)
    public void clickAgree() {
        tapListener.onTapAgree();
        dismiss();
    }

    @OnClick(R.id.btn_disgree)
    public void clickDisagree() {
        dismiss();
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btn_agree:
//                tapListener.onTapAgree();
//                dismiss();
//                break;
//            case R.id.btn_disgree:
//                dismiss();
//                break;
//        }
//    }

    public void setTapListener(TapListener tapListener) {
        this.tapListener = tapListener;
    }
}
