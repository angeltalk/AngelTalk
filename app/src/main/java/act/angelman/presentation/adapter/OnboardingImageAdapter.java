package act.angelman.presentation.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import act.angelman.R;

import static android.os.Build.VERSION_CODES.M;


public class OnboardingImageAdapter extends PagerAdapter{

    int[] ONBOARDING_IMAGES = {
            R.drawable.img_onboarding_1,
            R.drawable.img_onboarding_2,
            R.drawable.img_onboarding_3,
            R.drawable.img_onboarding_4,
            R.drawable.img_onboarding_5
    };

    private Context context;
    private View.OnClickListener permissionButtonOnClickListener;

    public OnboardingImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return ONBOARDING_IMAGES.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.item_onboarding,container,false);

        ImageView onBoardingImage = ((ImageView) layout.findViewById(R.id.img_onboarding));
        ImageView onBoardingFinishAngelee = ((ImageView) layout.findViewById(R.id.onboarding_finish_angelee));
        ImageView finishButton = ((ImageView) layout.findViewById(R.id.onboarding_finish));
        TextView privacyGuide = ((TextView) layout.findViewById(R.id.onboarding_privacy_guide));

        onBoardingImage.setImageDrawable(context.getResources().getDrawable(ONBOARDING_IMAGES[position]));
        finishButton.setOnClickListener(permissionButtonOnClickListener);


        if(isLastPage(position)) {
            setPrivacyGuideText(privacyGuide);
            privacyGuide.setVisibility(View.VISIBLE);
            finishButton.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT < M) {
                onBoardingFinishAngelee.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(R.drawable.angelee)
                        .asGif()
                        .crossFade()
                        .into(onBoardingFinishAngelee);
                onBoardingImage.setImageDrawable(context.getResources().getDrawable(R.drawable.img_onboarding_5_low_version));
                finishButton.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_appstart_low_version));
            }
        }

        container.addView(layout);
        return layout;
    }

    private void setPrivacyGuideText(TextView privacyGuide) {
        String privacyPolicy = context.getString(R.string.noti_privacy);
        String privacyGuideString = context.getString(R.string.onboarding_privacy_guide);

        SpannableStringBuilder builder = new SpannableStringBuilder(privacyGuideString);

        int start = privacyGuideString.indexOf(privacyPolicy);
        int end = start + privacyPolicy.length();

        builder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        builder.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        privacyGuide.append(builder);

        Linkify.TransformFilter mTransform = new Linkify.TransformFilter() {
            @Override
            public String transformUrl(Matcher matcher, String s) {
                return "";
            }
        };
        Linkify.addLinks(privacyGuide, Pattern.compile(privacyPolicy), context.getString(R.string.privacy_web_url), null, mTransform);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    public void setPermissionButtonOnClickListener(View.OnClickListener onClickListener) {
        permissionButtonOnClickListener = onClickListener;
    }

    private boolean isLastPage(int position) {
        return position == ONBOARDING_IMAGES.length - 1;
    }

}
