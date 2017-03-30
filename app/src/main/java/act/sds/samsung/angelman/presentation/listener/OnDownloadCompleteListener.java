package act.sds.samsung.angelman.presentation.listener;

import act.sds.samsung.angelman.domain.model.CardTransferModel;

public interface OnDownloadCompleteListener {

    void onSuccess(CardTransferModel cardModel, String downloadFilePath);

    void onFail();

}
