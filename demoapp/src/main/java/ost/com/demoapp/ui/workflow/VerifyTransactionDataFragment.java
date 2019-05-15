package ost.com.demoapp.ui.workflow;

import android.os.Bundle;

public class VerifyTransactionDataFragment extends WorkFlowVerifyDataFragment {

    public static VerifyTransactionDataFragment newInstance() {
        VerifyTransactionDataFragment fragment = new VerifyTransactionDataFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    String getVerifyDataHeading() {
        return "Transaction";
    }

    String getPositiveButtonText() {
        return "Authorize Transaction";
    }

    String getSubHeading() {
        return "You’ve a transaction authorization\n request of following transaction";
    }

    String getTitle() {
        return "Authorize Transaction";
    }
}
