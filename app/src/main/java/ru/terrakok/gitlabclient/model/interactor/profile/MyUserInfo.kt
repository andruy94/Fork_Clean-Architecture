package ru.terrakok.gitlabclient.model.interactor.profile

import ru.terrakok.gitlabclient.entity.User

/**
 * @author Konstantin Tskhovrebov (aka terrakok) on 11.05.17.
 */

data class MyUserInfo(val user: User?, val serverName: String)