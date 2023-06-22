package com.vkontakte.miracle.fragment.messages;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.MiracleFragment;
import com.miracle.engine.recyclerview.CustomAdapterListUpdateCallback;
import com.miracle.engine.recyclerview.LoadableAdapter;
import com.miracle.engine.recyclerview.TypedData;
import com.miracle.engine.recyclerview.viewholder.bundle.ViewHolderBundle;
import com.miracle.widget.ExtendedAppCompatButton;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.general.Error;
import com.vkontakte.miracle.model.general.ExtendedArrays;
import com.vkontakte.miracle.model.groups.Group;
import com.vkontakte.miracle.model.messages.Conversation;
import com.vkontakte.miracle.model.messages.fields.ChatSettings;
import com.vkontakte.miracle.model.messages.fields.Peer;
import com.vkontakte.miracle.model.messages.fields.PushSettings;
import com.vkontakte.miracle.model.users.Profile;
import com.vkontakte.miracle.model.users.fileds.LastSeen;
import com.vkontakte.miracle.util.ConversationUtil;
import com.vkontakte.miracle.util.CountUtil;
import com.vkontakte.miracle.util.OnlineUtil;
import com.vkontakte.miracle.util.constants.TypedDataConstants;
import com.vkontakte.miracle.view.messages.ConversationHeaderChip;
import com.vkontakte.miracle.viewholder.general.ErrorViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionCreateViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionInviteUserViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionKickUserViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionPhotoUpdateViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionRecordingVoiceMessageViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionTitleUpdateViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.action.ChatActionTypingMessageViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.message.MessageInViewHolderBundle;
import com.vkontakte.miracle.viewholder.messages.chat.message.MessageOutViewHolderBundle;

import java.util.ArrayList;
import java.util.Map;

public class FragmentChat extends MiracleFragment {

    private ChatViewModel chatViewModel;
    private Conversation conversation;
    private ExtendedArrays extendedArrays;
    private String peerId = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        if(chatViewModel.getPeerId()==null){
            Bundle args = getArguments();
            if(args!=null&&!args.isEmpty()){
                peerId = args.getString("peerId");
            }
            chatViewModel.setPeerId(peerId);
        } else {
            peerId = chatViewModel.getPeerId();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chat_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindContent(view);
    }

    private void bindContent(View view){
        ConversationHeaderChip conversationHeaderChip = view.findViewById(R.id.conversationChip);

        ProgressBar progressBar = view.findViewById(R.id.circleProgressBar);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,true));

        Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = getAdapterBundles();

        LoadableAdapter adapter = new LoadableAdapter(bundles);
        adapter.setListUpdateCallback(new CustomAdapterListUpdateCallback(adapter));
        adapter.setLastItemCountFromEnd(4);
        adapter.setOnLastItemBindListener(() -> chatViewModel.tryLoadMore());
        recyclerView.setAdapter(adapter);

        chatViewModel.loadingType.observe(requireActivity(), loadingType -> {
            switch (loadingType){
                case NO_LOADING:
                case LOADING_NEXT: {
                    progressBar.setVisibility(View.GONE);
                    break;
                }
                case FIRST_TIME:{
                    progressBar.setVisibility(View.VISIBLE);
                    break;
                }
            }
        });

        chatViewModel.viewState.observe(requireActivity(), viewState -> {
            if(viewState instanceof ChatViewModel.ViewState.Loading){
                clearAdapter(adapter);
            } else if(viewState instanceof ChatViewModel.ViewState.Success){
                ChatViewModel.ViewState.Success success = (ChatViewModel.ViewState.Success)viewState;
                conversation = success.getConversation();
                extendedArrays = success.getExtendedArrays();
                //////////////////////////////////////////////
                bindConversationChip(conversationHeaderChip, conversation);
                bindAdapter(adapter, recyclerView, success);
            } else if(viewState instanceof ChatViewModel.ViewState.Empty){
                bindAdapterWidthPlaceHolder(adapter);
            } else if(viewState instanceof ChatViewModel.ViewState.Error){
                ChatViewModel.ViewState.Error error = (ChatViewModel.ViewState.Error)viewState;
                bindAdapter(adapter, error);
            }
        });
    }

    private void bindAdapter(LoadableAdapter adapter, RecyclerView recyclerView, ChatViewModel.ViewState.Success success){
        ArrayList<TypedData> items = new ArrayList<>(success.getMessages());
        if(success.getChatAction()!=null) {
            items.add(0,success.getChatAction());
        }
        //////////////////////////////////////////////
        boolean onBottom = !recyclerView.canScrollVertically(1);
        adapter.setItems(items);
        adapter.setLoading(success.hasNext());
        if(onBottom){
            recyclerView.scrollToPosition(0);
        }
    }

    private void bindAdapter(LoadableAdapter adapter, ChatViewModel.ViewState.Error error){
        ArrayList<TypedData> items = new ArrayList<>();
        items.add(new Error(error.getErrorMessage()));
        adapter.setItems(items);
        adapter.setLoading(false);
    }

    private void bindAdapterWidthPlaceHolder(LoadableAdapter adapter){
        ArrayList<TypedData> items = new ArrayList<>();
        items.add(() -> TypedDataConstants.TYPE_PLACEHOLDER);
        adapter.setItems(items);
        adapter.setLoading(false);
    }

    private void clearAdapter(LoadableAdapter adapter){
        adapter.setItems(null);
        adapter.setLoading(false);
    }

    private void bindConversationChip(ConversationHeaderChip conversationHeaderChip, Conversation conversation){
        String photo = null;
        String title = null;
        String subtitle = null;
        boolean verified = false;
        boolean muted = false;
        boolean online = false;
        boolean mobile = false;

        PushSettings pushSettings = conversation.getPushSettings();
        Peer peer = conversation.getPeer();
        switch (peer.getType()){
            case "user":{
                Profile profile = extendedArrays.getProfiles().get(peer.getId());
                if(profile!=null) {
                    title = profile.getFullName();
                    photo = profile.getPhoto200();
                    verified = profile.isVerified();
                    online =  profile.isOnline();
                    LastSeen lastSeen = profile.getLastSeen();
                    if(lastSeen!=null){
                        mobile = profile.getLastSeen().getPlatform()<6;
                        subtitle = OnlineUtil.getOnlineString(online,lastSeen.getTime(),profile.getSex(),requireContext());
                    }

                }
                break;
            }
            case "group":{
                Group group = extendedArrays.getGroups().get(peer.getId());
                if(group!=null) {
                    title = group.getName();
                    subtitle = requireContext().getString(R.string.group);
                    photo = group.getPhoto200();
                    verified = group.isVerified();
                }
                break;
            }
            case "chat":{
                ChatSettings chatSettings = conversation.getChatSettings();
                if(chatSettings!=null){
                    title = chatSettings.getTitle();
                    subtitle = CountUtil.getMembersCount(chatSettings.getMembersCount(), requireContext());
                    if(chatSettings.getPhoto()!=null){
                        photo = chatSettings.getPhoto().getPhoto200();
                    }
                }
                break;
            }
            case "email":{
                break;
            }
        }

        if(pushSettings!=null){
            muted = pushSettings.isDisabledForever()||pushSettings.isNoSound();
        }

        conversationHeaderChip.setTitleText(title);
        conversationHeaderChip.setSubtitleText(subtitle);
        conversationHeaderChip.setVerified(verified);
        conversationHeaderChip.setMuted(muted);
        conversationHeaderChip.setOnline(online, mobile);

        ImageView conversationPhoto = conversationHeaderChip.getAvatarImage();
        Picasso.get().cancelRequest(conversationPhoto);
        if(photo==null||photo.isEmpty()){
            conversationPhoto.setImageDrawable(
                    ConversationUtil.getConversationAvatarPlaceHolder(title, requireContext()));
        } else {
            Picasso.get().load(photo).into(conversationPhoto);
        }

        conversationHeaderChip.requestLayout();
        conversationHeaderChip.invalidate();
    }

    private Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> getAdapterBundles(){
        Map<Integer, ViewHolderBundle<RecyclerView.ViewHolder, Object>> bundles = new ArrayMap<>();

        MessageOutViewHolderBundle messageOutViewHolderBundle = new MessageOutViewHolderBundle() {

            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }

            @Override
            public Conversation requestConversation() {
                return conversation;
            }
        };
        bundles.put(TypedDataConstants.TYPE_MESSAGE_OUT,messageOutViewHolderBundle);

        MessageInViewHolderBundle messageInViewHolderBundle = new MessageInViewHolderBundle() {
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }

            @Override
            public Conversation requestConversation() {
                return conversation;
            }
        };
        bundles.put(TypedDataConstants.TYPE_MESSAGE_IN,messageInViewHolderBundle);

        ChatActionCreateViewHolderBundle chatActionCreateViewHolderBundle = new ChatActionCreateViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_CREATE, chatActionCreateViewHolderBundle);

        ChatActionInviteUserViewHolderBundle chatActionInviteUserViewHolderBundle = new ChatActionInviteUserViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_INVITE_USER, chatActionInviteUserViewHolderBundle);

        ChatActionKickUserViewHolderBundle chatActionKickUserViewHolderBundle = new ChatActionKickUserViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_KICK_USER, chatActionKickUserViewHolderBundle);

        ChatActionPhotoUpdateViewHolderBundle chatActionPhotoUpdateViewHolderBundle = new ChatActionPhotoUpdateViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_PHOTO_UPDATE, chatActionPhotoUpdateViewHolderBundle);

        ChatActionTitleUpdateViewHolderBundle chatActionTitleUpdateViewHolderBundle = new ChatActionTitleUpdateViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_TITLE_UPDATE, chatActionTitleUpdateViewHolderBundle);

        ChatActionTypingMessageViewHolderBundle chatActionTypingMessageViewHolderBundle = new ChatActionTypingMessageViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_TYPING_MESSAGE, chatActionTypingMessageViewHolderBundle);

        ChatActionRecordingVoiceMessageViewHolderBundle chatActionRecordingVoiceMessageViewHolderBundle = new ChatActionRecordingVoiceMessageViewHolderBundle(){
            @Override
            public ExtendedArrays requestExtendedArrays() {
                return extendedArrays;
            }
        };
        bundles.put(TypedDataConstants.TYPE_CHAT_ACTION_RECORDING_VOICE_MESSAGE, chatActionRecordingVoiceMessageViewHolderBundle);

        ErrorViewHolderBundle errorViewHolderBundle = new ErrorViewHolderBundle() {};
        bundles.put(TypedDataConstants.TYPE_ERROR,errorViewHolderBundle);

        return bundles;
    }

}
