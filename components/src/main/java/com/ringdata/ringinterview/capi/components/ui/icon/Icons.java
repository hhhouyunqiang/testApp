package com.ringdata.ringinterview.capi.components.ui.icon;

import com.joanzapata.iconify.Icon;

/**
 * Created by admin on 17/10/10.
 */

public enum Icons implements Icon {
    icon_user('\ue800'),
    icon_password('\ue801'),
    icon_language('\ue802'),
    icon_search('\ue803'),
    icon_setting('\ue804'),

    icon_user_o('\uf2c0'),
    icon_angle_down('\uf107'),
    icon_angle_up('\uf106'),
    icon_left_open('\uf007'),
    icon_right_open('\uf006'),

    icon_sync('\ue805'),
    icon_pencil('\ue806'),
    icon_building('\uf0f7'),

    icon_cadi('\ue807'),
    icon_caxi('\uf00a'),
    icon_capi('\ue809'),
    icon_cati('\ue80a'),
    icon_cawi('\uf26b'),

    icon_visit_ok('\ue80b'),
    icon_next_visit('\uf274'),
    icon_refuse_visit('\ue80c'),

    icon_info('\uf086'),
    icon_edit('\ue808'),
    icon_map('\ue80d'),

    icon_add('\uf0fe'),
    icon_wechat('\uf1d7'),
    icon_qq('\ue60a'),
    icon_delete('\ue814'),
    icon_link('\ue815'),
    icon_save('\ue813'),
    icon_download('\ue811'),
    icon_upload('\ue812'),
    icon_clock('\ue810'),
    icon_cancel('\ue80f'),
    icon_qrcode('\ue80e'),
    icon_menu('\uf0c9'),
    icon_font('\ue819'),
    icon_mic('\uf130'),
    icon_left_black('\uf137'),
    icon_right_black('\uf138'),
    icon_upgrade('\ue816'),
    icon_question('\uf29c'),
    icon_check('\ue817'),
    icon_setting_o('\ue818');

    private char character;

    Icons(char character) {
        this.character = character;
    }

    @Override
    public String key() {
        return name().replace('_', '-');
    }

    @Override
    public char character() {
        return character;
    }
}
