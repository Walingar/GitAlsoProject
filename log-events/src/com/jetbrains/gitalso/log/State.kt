package com.jetbrains.gitalso.log

enum class State {
    BEFORE_COMMIT,
    SHOW_MAIN_DIALOG,
    SHOW_MODIFIED,
    SHOW_UNMODIFIED,
    AFTER_COMMIT,
    NOT_INDEXED,
    NOT_SHOWED,
    A_LOT_OF_FILES
}