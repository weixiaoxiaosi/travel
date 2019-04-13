package com.wanjia.service;

import com.wanjia.domain.Favorite;
import com.wanjia.domain.User;

public interface IFavoriteService {
    Integer findAddFavorite(String rid, User user);

    Integer findByFavorite(String rid, User user);

    Favorite findFavorite(String rid, User user)throws Exception;
}
