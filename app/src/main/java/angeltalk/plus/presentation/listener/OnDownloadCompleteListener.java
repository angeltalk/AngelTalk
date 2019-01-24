package angeltalk.plus.presentation.listener;

import angeltalk.plus.domain.model.CardTransferModel;

public interface OnDownloadCompleteListener {

    void onSuccess(CardTransferModel cardModel, String downloadFilePath);

    void onFail();

}
