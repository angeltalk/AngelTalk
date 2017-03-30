package act.sds.samsung.angelman.presentation.listener;

import act.sds.samsung.angelman.domain.model.CardModel;

public interface OnDownloadCompleteListener {

    void onSuccess(CardModel cardModel);

    void onFail();

}
