package com.vkontakte.miracle.network.vkapi;

import static com.vkontakte.miracle.network.client.ClientFactory.createAuthMethodRetro;
import static com.vkontakte.miracle.network.client.ClientFactory.createAuthRetro;
import static com.vkontakte.miracle.network.client.ClientFactory.createLongPollRetro;
import static com.vkontakte.miracle.network.client.ClientFactory.createVKAPIMethodRetro;

import com.vkontakte.miracle.network.service.IAuth;
import com.vkontakte.miracle.network.service.IExecute;
import com.vkontakte.miracle.network.service.ILongPoll;
import com.vkontakte.miracle.network.service.IMessages;
import com.vkontakte.miracle.network.service.IUsers;

public class APIMethodsFactory {

    public static IAuth auth(){
        return createAuthRetro().create(IAuth.class);
    }

    public static IAuth authMethods(){
        return createAuthMethodRetro().create(IAuth.class);
    }

    public static IUsers users(){
        return createVKAPIMethodRetro().create(IUsers.class);
    }

    public static IMessages messages(){
        return createVKAPIMethodRetro().create(IMessages.class);
    }

    public static IExecute execute(){
        return createVKAPIMethodRetro().create(IExecute.class);
    }

    public static ILongPoll longPoll(){
        return createLongPollRetro().create(ILongPoll.class);
    }


}
