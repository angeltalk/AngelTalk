package act.sds.samsung.angelman.presentation.util;

import act.sds.samsung.angelman.R;

public class ResourceMapper {

    public enum IconType {
        HOSPITAL,
        HAND,
        DOG,
        TSHIRT,
        MOVIE,
        STAR,
        SMILE,
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
                    R.drawable.icon_hospital_unselect,
                    R.drawable.icon_hospital_select,
                    R.drawable.icon_hospital_used
            },
            {
                    R.drawable.icon_hand_menu,
                    R.drawable.icon_hand_unselect,
                    R.drawable.icon_hand_select,
                    R.drawable.icon_hand_used
            },
            {
                    R.drawable.icon_dog_menu,
                    R.drawable.icon_dog_unselect,
                    R.drawable.icon_dog_select,
                    R.drawable.icon_dog_used
            },
            {
                    R.drawable.icon_tshirt_menu,
                    R.drawable.icon_tshirt_unselect,
                    R.drawable.icon_tshirt_select,
                    R.drawable.icon_tshirt_used
            },
            {
                    R.drawable.icon_movie_menu,
                    R.drawable.icon_movie_unselect,
                    R.drawable.icon_movie_select,
                    R.drawable.icon_movie_used
            },
            {
                    R.drawable.icon_star_menu,
                    R.drawable.icon_star_unselect,
                    R.drawable.icon_star_select,
                    R.drawable.icon_star_used
            },
            {
                    R.drawable.icon_smile_menu,
                    R.drawable.icon_smile_unselect,
                    R.drawable.icon_smile_select,
                    R.drawable.icon_smile_used
            },
            {
                    R.drawable.icon_puzzle_menu,
                    R.drawable.icon_puzzle_unselect,
                    R.drawable.icon_puzzle_select,
                    R.drawable.icon_puzzle_used
            },
            {
                    R.drawable.icon_food_menu,
                    R.drawable.icon_food_unselect,
                    R.drawable.icon_food_select,
                    R.drawable.icon_food_used
            },
            {
                    R.drawable.icon_bus_menu,
                    R.drawable.icon_bus_unselect,
                    R.drawable.icon_bus_select,
                    R.drawable.icon_bus_used
            },
            {
                    R.drawable.icon_school_menu,
                    R.drawable.icon_school_unselect,
                    R.drawable.icon_school_select,
                    R.drawable.icon_school_used
            },
            {
                    R.drawable.icon_friend_menu,
                    R.drawable.icon_friend_unselect,
                    R.drawable.icon_friend_select,
                    R.drawable.icon_friend_used
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
