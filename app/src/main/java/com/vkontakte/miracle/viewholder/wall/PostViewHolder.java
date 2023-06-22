package com.vkontakte.miracle.viewholder.wall;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.vkontakte.miracle.util.CountUtil.reduceTheNumber;

import android.text.method.LinkMovementMethod;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.recyclerview.DynamicBindingAdapter;
import com.miracle.engine.recyclerview.GlobalCornerRadiusDecoration;
import com.miracle.engine.recyclerview.TypedData;
import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.miracle.engine.recyclerview.asymmetricgrid.AsymmetricGridLayoutManager;
import com.miracle.engine.recyclerview.asymmetricgrid.AsymmetricGridSpacingDecoration;
import com.miracle.engine.recyclerview.asymmetricgrid.GridHelper;
import com.miracle.engine.util.DimensionUtil;
import com.miracle.engine.util.StringsUtil;
import com.miracle.widget.ExtendedMaterialButton;
import com.miracle.widget.ExtendedTextView;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.Attachments;
import com.vkontakte.miracle.model.general.Comments;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.general.Likes;
import com.vkontakte.miracle.model.general.Owner;
import com.vkontakte.miracle.model.general.Reposts;
import com.vkontakte.miracle.model.photos.Photo;
import com.vkontakte.miracle.model.wall.Post;
import com.vkontakte.miracle.model.wall.fields.Views;
import com.vkontakte.miracle.util.TimeUtil;
import com.vkontakte.miracle.util.constants.TypedDataConstants;
import com.vkontakte.miracle.view.text.VKTextView;
import com.vkontakte.miracle.viewholder.photos.PhotoViewHolder;
import com.vkontakte.miracle.viewholder.photos.PhotoViewHolderBundle;

import java.util.ArrayList;
import java.util.List;

public class PostViewHolder extends RecyclerView.ViewHolder {

    //------------------------------------------//

    private final RecyclerView.RecycledViewPool photosRecycledViewPool;

    //------------------------------------------//

    private OnPostActionsListener onPostActionsListener;

    //------------------------------------------//

    private Post post;
    private Owner owner;

    //------------------------------------------//

    private final ImageView ownerImage;
    private final TextView ownerName;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private final TextView date;

    private final ViewStub textStub;
    private VKTextView text;

    private final ViewStub photosRecyclerViewVS;
    private RecyclerView photosRecyclerView;
    private DynamicBindingAdapter photosAdapter;
    private AsymmetricGridLayoutManager photosLayoutManager;
    private AsymmetricGridSpacingDecoration photosDecoration;

    private final ExtendedMaterialButton likeButton;
    private final ExtendedMaterialButton commentButton;
    private final ExtendedMaterialButton repostButton;
    private final ExtendedTextView viewsCounter;

    public PostViewHolder(@NonNull View itemView,
                          RecyclerView.RecycledViewPool photosRecycledViewPool) {
        super(itemView);

        this.photosRecycledViewPool = photosRecycledViewPool;

        //------------------------------------------//

        FrameLayout header = itemView.findViewById(R.id.post_header);

        RelativeLayout ownerChip = header.findViewById(R.id.ownerChip);
        ownerImage = ownerChip.findViewById(R.id.ownerPhoto);
        ownerName = ownerChip.findViewById(R.id.ownerName);
        verifiedStub = ownerChip.findViewById(R.id.verifiedStub);
        date = ownerChip.findViewById(R.id.date);

        textStub = itemView.findViewById(R.id.textVS);

        photosRecyclerViewVS = itemView.findViewById(R.id.photosRecyclerViewVS);

        likeButton = itemView.findViewById(R.id.likeButton);
        commentButton = itemView.findViewById(R.id.commentButton);
        repostButton = itemView.findViewById(R.id.repostButton);
        viewsCounter = itemView.findViewById(R.id.viewsCounter);

        //------------------------------------------//

        ownerChip.setOnClickListener(v -> {
            if(owner!=null&&onPostActionsListener!=null){
                onPostActionsListener.onOwnerLinkClick(owner.getId());
            }
        });
    }

    public void bind(Post post, ExtendedArrays extendedArrays){

        this.post = post;

        Owner owner = extendedArrays.findOwnerById(post.getFromId());
        if(owner==null){
            owner = extendedArrays.findOwnerById(post.getSourceId());
        }
        if(owner==null){
            owner = extendedArrays.findOwnerById(post.getOwnerId());
        }

        this.owner = owner;

        bindHeader(owner, post);

        //--------------------TEXT-BINDING-----------------//

        if(post.getText().isEmpty()){
            hideText();
        } else {
            showText();
            text.setText(post.getText());
        }

        //----------------ATTACHMENTS-BINDING--------------//

        Attachments attachments = post.getAttachments();
        if(attachments!=null){

            //-----------------GRID-BINDING----------------//

            GridHelper.GridBundle gridBundle = attachments.getGridBundle();
            if(gridBundle != null){
                showPhotos();
                photosDecoration.setGridEdgesMasks(gridBundle.getGridEdgesMasks());
                photosLayoutManager.setPositions(gridBundle.getGrid());
                ArrayList<TypedData> typedData = new ArrayList<>();
                typedData.addAll(attachments.getPhotos());
                photosAdapter.setItems(typedData);
            } else {
                hidePhotos();
            }
        } else {
            hidePhotos();
        }

        bindFooter(post);

    }

    private void bindHeader(Owner owner, Post post){
        //------------------OWNER-BINDING------------------//
        Picasso.get().cancelRequest(ownerImage);
        if(owner!=null) {
            if (owner.getPhoto200().isEmpty()) {
                ownerImage.setImageBitmap(null);
            } else {
                Picasso.get().load(owner.getPhoto200()).into(ownerImage);
            }
            ownerName.setText(owner.getFullName());
            if (owner.isVerified()) showVerified(); else hideVerified();
        } else {
            ownerImage.setImageBitmap(null);
            ownerName.setText("");
            hideVerified();
        }
        //--------------------DATE-BINDING-----------------//
        date.setText(StringsUtil.upperCaseFirstLetter(
                TimeUtil.getRelativeDateString(itemView.getContext(), post.getDate(), true)));
    }

    private void bindFooter(Post post){

        Likes likes = post.getLikes();

        Comments comments = post.getComments();
        commentButton.setText(reduceTheNumber(comments.getCount()));
        commentButton.setEnabled(comments.isCanView());

        if(!likes.isRepostDisabled()) {
            if(repostButton.getVisibility()!=VISIBLE) {
                repostButton.setVisibility(VISIBLE);
            }
            Reposts reposts = post.getReposts();
            repostButton.setText(reduceTheNumber(reposts.getCount()));
            repostButton.setEnabled(likes.isCanPublish());
        } else {
            if(repostButton.getVisibility()!=GONE) {
                repostButton.setVisibility(GONE);
            }
        }

        Views views = post.getViews();
        if (views != null) {
            if(viewsCounter.getVisibility()!=VISIBLE) {
                viewsCounter.setVisibility(VISIBLE);
            }
            viewsCounter.setText(reduceTheNumber(views.getCount()));
        } else {
            if(viewsCounter.getVisibility()!=GONE) {
                viewsCounter.setVisibility(GONE);
            }
        }
    }

    //------------------------------------------//

    private void stubVerified(){
        if(verified==null) {
            if(verifiedStub!=null) {
                verified = (ImageView) verifiedStub.inflate();
            } else {
                verified = itemView.findViewById(R.id.verified);
            }
        }
    }

    private void showVerified(){
        stubVerified();
        if (verified.getVisibility() != VISIBLE) {
            verified.setVisibility(VISIBLE);
        }
    }

    private void hideVerified(){
        if (verified != null && verified.getVisibility() != GONE) {
            verified.setVisibility(GONE);
        }
    }

    //------------------------------------------//

    private void stubText(){
        if(text==null) {
            if(textStub!=null) {
                text = (VKTextView) textStub.inflate();
            } else {
                text = itemView.findViewById(R.id.text);
            }
            if(text!=null){
                text.setOnUrlClickListener(url -> {
                    if(onPostActionsListener!=null){
                        onPostActionsListener.onUrlLinkClick(url);
                    }
                });
                text.setOnHashtagClickListener(hashtag -> {
                    if(onPostActionsListener!=null){
                        onPostActionsListener.onHashTagLinkClick(hashtag);
                    }
                });
                text.setOnOwnerClickListener(ownerId -> {
                    if(onPostActionsListener!=null){
                        onPostActionsListener.onOwnerLinkClick(ownerId);
                    }
                });
                text.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    private void showText(){
        stubText();
        if (text.getVisibility() != VISIBLE) {
            text.setVisibility(VISIBLE);
        }
    }

    private void hideText(){
        if (text != null && text.getVisibility() != GONE) {
            text.setVisibility(GONE);
        }
    }

    //------------------------------------------//

    private void stubPhotos(){
        if(photosRecyclerView==null) {
            if(photosRecyclerViewVS!=null) {
                photosRecyclerView = (RecyclerView) photosRecyclerViewVS.inflate();
            } else {
                photosRecyclerView = itemView.findViewById(R.id.photosRecyclerView);
            }
            if(photosRecyclerView!=null){
                if(photosRecycledViewPool!=null) {
                    photosRecyclerView.setRecycledViewPool(photosRecycledViewPool);
                }
                photosRecyclerView.setNestedScrollingEnabled(false);
                photosRecyclerView.setHasFixedSize(false);
                photosRecyclerView.setItemAnimator(null);
                photosLayoutManager = new AsymmetricGridLayoutManager();
                photosDecoration = new AsymmetricGridSpacingDecoration(
                        (int) DimensionUtil.dpToPx(itemView.getContext(),3));


                ArrayMap<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = new ArrayMap<>();

                PhotoViewHolderBundle photoViewHolderBundle = new PhotoViewHolderBundle() {

                    @Override
                    public void onBind(PhotoViewHolder photoViewHolder, Photo photo) {
                        photoViewHolder.itemView.setTransitionName(String.format("photo_%s_%s_%s",
                                post.getId(),photo.getId(),photo.getAlbumId()));
                    }

                    @Override
                    public void onPhotoClick(Photo photo, View photoView) {
                        if(onPostActionsListener!=null){
                            Attachments attachments = post.getAttachments();
                            if(attachments!=null){
                                onPostActionsListener.onPostPhotoClick(photo, attachments.getPhotos(), photoView);
                            }
                        }
                    }
                };

                bundles.put(TypedDataConstants.TYPE_PHOTO, photoViewHolderBundle);

                photosAdapter = new DynamicBindingAdapter(bundles);

                photosRecyclerView.addItemDecoration(new GlobalCornerRadiusDecoration(
                        (int)itemView.getContext().getResources().getDimension(R.dimen.round_2)));
                photosRecyclerView.addItemDecoration(photosDecoration);
                photosRecyclerView.setLayoutManager(photosLayoutManager);
                photosRecyclerView.setAdapter(photosAdapter);
            }
        }
    }

    private void showPhotos(){
        stubPhotos();
        if (photosRecyclerView.getVisibility() != VISIBLE) {
            photosRecyclerView.setVisibility(VISIBLE);
        }
    }

    private void hidePhotos(){
        if (photosRecyclerView != null && photosRecyclerView.getVisibility() != GONE) {
            photosRecyclerView.setVisibility(GONE);
        }
    }

    //------------------------------------------//

    public void setOnPostActionsListener(OnPostActionsListener onPostActionsListener) {
        this.onPostActionsListener = onPostActionsListener;
    }

    public interface OnPostActionsListener{

        void onPostPhotoClick(Photo photo, List<Photo> photos, View view);

        void onOwnerLinkClick(String ownerId);

        void onUrlLinkClick(String url);

        void onHashTagLinkClick(String hashTag);

    }
}
