package act.angelman.presentation.util;

import act.angelman.R;

public class ResourceMapper {

    public enum IconType {
        HOSPITAL,
        HAND,
        DOG,
        TSHIRT,
        MOVIE,
        PUZZLE,
        FOOD,
        BUS,
        SCHOOL,
        FRIEND
    }

    public enum IconState {
        DEFAULT,
        UNSELECT,
        SELECT,
        USED
    }

    public enum ColorType {
        RED,
        ORANGE,
        YELLOW,
        GREEN,
        BLUE,
        PURPLE
    }

    public enum ColorState {
        SELECT,
        UNSELECT,
        USED,
        MENU
    }

    static int iconMappingTable[][] = {
            {
                    R.drawable.icon_hospital_menu,
                    R.drawable.icon_hospital_unselect_dark,
                    R.drawable.icon_hospital_select_dark,
                    R.drawable.icon_hospital_used_dark
            },
            {
                    R.drawable.icon_hand_menu,
                    R.drawable.icon_hand_unselect_dark,
                    R.drawable.icon_hand_select_dark,
                    R.drawable.icon_hand_used_dark
            },
            {
                    R.drawable.icon_dog_menu,
                    R.drawable.icon_dog_unselect_dark,
                    R.drawable.icon_dog_select_dark,
                    R.drawable.icon_dog_used_dark
            },
            {
                    R.drawable.icon_tshirt_menu,
                    R.drawable.icon_tshirt_unselect_dark,
                    R.drawable.icon_tshirt_select_dark,
                    R.drawable.icon_tshirt_used_dark
            },
            {
                    R.drawable.icon_movie_menu,
                    R.drawable.icon_movie_unselect_dark,
                    R.drawable.icon_movie_select_dark,
                    R.drawable.icon_movie_used_dark
            },
            {
                    R.drawable.icon_puzzle_menu,
                    R.drawable.icon_puzzle_unselect_dark,
                    R.drawable.icon_puzzle_select_dark,
                    R.drawable.icon_puzzle_used_dark
            },
            {
                    R.drawable.icon_food_menu,
                    R.drawable.icon_food_unselect_dark,
                    R.drawable.icon_food_select_dark,
                    R.drawable.icon_food_used_dark
            },
            {
                    R.drawable.icon_bus_menu,
                    R.drawable.icon_bus_unselect_dark,
                    R.drawable.icon_bus_select_dark,
                    R.drawable.icon_bus_used_dark
            },
            {
                    R.drawable.icon_school_menu,
                    R.drawable.icon_school_unselect_dark,
                    R.drawable.icon_school_select_dark,
                    R.drawable.icon_school_used_dark
            },
            {
                    R.drawable.icon_friend_menu,
                    R.drawable.icon_friend_unselect_dark,
                    R.drawable.icon_friend_select_dark,
                    R.drawable.icon_friend_used_dark
            },
    };

    static int colorMappingTable[][] = {
            {
                    R.drawable.icon_color_red_select,
                    R.drawable.icon_color_red_unselect,
                    R.drawable.icon_color_red_used,
                    R.drawable.background_gradient_red
            },
            {
                    R.drawable.icon_color_orange_select,
                    R.drawable.icon_color_orange_unselect,
                    R.drawable.icon_color_orange_used,
                    R.drawable.background_gradient_orange
            },
            {
                    R.drawable.icon_color_yellow_select,
                    R.drawable.icon_color_yellow_unselect,
                    R.drawable.icon_color_yellow_used,
                    R.drawable.background_gradient_yellow
            },
            {
                    R.drawable.icon_color_green_select,
                    R.drawable.icon_color_green_unselect,
                    R.drawable.icon_color_green_used,
                    R.drawable.background_gradient_green
            },
            {
                    R.drawable.icon_color_blue_select,
                    R.drawable.icon_color_blue_unselect,
                    R.drawable.icon_color_blue_used,
                    R.drawable.background_gradient_blue
            },
            {
                    R.drawable.icon_color_purple_select,
                    R.drawable.icon_color_purple_unselect,
                    R.drawable.icon_color_purple_used,
                    R.drawable.background_gradient_purple
            },

    };

    public static int getCategoryIconResourceId(int type, int state) {
        return iconMappingTable[type][state];
    }

    public static int getCategoryColorResourceId(int type, int state) {
        return colorMappingTable[type][state];
    }
}
