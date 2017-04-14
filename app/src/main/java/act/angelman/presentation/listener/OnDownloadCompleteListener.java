package act.angelman.presentation.listener;

import act.angelman.domain.model.CardTransferModel;

public interface OnDownloadCompleteListener {

    void onSuccess(CardTransferModel cardModel, String downloadFilePath);

    void onFail();

}
