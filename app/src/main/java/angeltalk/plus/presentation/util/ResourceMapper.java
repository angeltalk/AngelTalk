package angeltalk.plus.presentation.util;

import angeltalk.plus.R;

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
        FRIEND,
        HOME,
        MUSIC,
        SPORTS,
        BOOK,
        BATH,
        SLEEP,
        PARK,
        SHOPPING,
        PHONE,
        GAME,
        ART,
        DRINK,
        CAKE,
        CAR,
        PLANE,
        SWIM,
        STAR,
        HEART,
        CAMERA,
        FLOWER
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
        PURPLE,
        PINK,
        TEAL,
        NAVY,
        BROWN,
        CORAL,
        AMBER,
        LIME,
        CYAN,
        INDIGO,
        ROSE,
        SKYBLUE,
        SAGE,
        GOLD,
        LAVENDER,
        MINT,
        SLATE,
        PEACH,
        VIOLET,
        CHERRY,
        FOREST,
        OCEAN,
        RUST,
        PLUM,
        CARAMEL
    }

    public enum ColorState {
        SELECT,
        UNSELECT,
        USED,
        MENU
    }

    static int iconMappingTable[][] = {
            {R.drawable.icon_hospital_menu, R.drawable.icon_hospital_unselect_dark, R.drawable.icon_hospital_select_dark, R.drawable.icon_hospital_used_dark},
            {R.drawable.icon_hand_menu, R.drawable.icon_hand_unselect_dark, R.drawable.icon_hand_select_dark, R.drawable.icon_hand_used_dark},
            {R.drawable.icon_dog_menu, R.drawable.icon_dog_unselect_dark, R.drawable.icon_dog_select_dark, R.drawable.icon_dog_used_dark},
            {R.drawable.icon_tshirt_menu, R.drawable.icon_tshirt_unselect_dark, R.drawable.icon_tshirt_select_dark, R.drawable.icon_tshirt_used_dark},
            {R.drawable.icon_movie_menu, R.drawable.icon_movie_unselect_dark, R.drawable.icon_movie_select_dark, R.drawable.icon_movie_used_dark},
            {R.drawable.icon_puzzle_menu, R.drawable.icon_puzzle_unselect_dark, R.drawable.icon_puzzle_select_dark, R.drawable.icon_puzzle_used_dark},
            {R.drawable.icon_food_menu, R.drawable.icon_food_unselect_dark, R.drawable.icon_food_select_dark, R.drawable.icon_food_used_dark},
            {R.drawable.icon_bus_menu, R.drawable.icon_bus_unselect_dark, R.drawable.icon_bus_select_dark, R.drawable.icon_bus_used_dark},
            {R.drawable.icon_school_menu, R.drawable.icon_school_unselect_dark, R.drawable.icon_school_select_dark, R.drawable.icon_school_used_dark},
            {R.drawable.icon_friend_menu, R.drawable.icon_friend_unselect_dark, R.drawable.icon_friend_select_dark, R.drawable.icon_friend_used_dark},
            {R.drawable.icon_home_menu, R.drawable.icon_home_unselect_dark, R.drawable.icon_home_select_dark, R.drawable.icon_home_used_dark},
            {R.drawable.icon_music_menu, R.drawable.icon_music_unselect_dark, R.drawable.icon_music_select_dark, R.drawable.icon_music_used_dark},
            {R.drawable.icon_sports_menu, R.drawable.icon_sports_unselect_dark, R.drawable.icon_sports_select_dark, R.drawable.icon_sports_used_dark},
            {R.drawable.icon_book_menu, R.drawable.icon_book_unselect_dark, R.drawable.icon_book_select_dark, R.drawable.icon_book_used_dark},
            {R.drawable.icon_bath_menu, R.drawable.icon_bath_unselect_dark, R.drawable.icon_bath_select_dark, R.drawable.icon_bath_used_dark},
            {R.drawable.icon_sleep_menu, R.drawable.icon_sleep_unselect_dark, R.drawable.icon_sleep_select_dark, R.drawable.icon_sleep_used_dark},
            {R.drawable.icon_park_menu, R.drawable.icon_park_unselect_dark, R.drawable.icon_park_select_dark, R.drawable.icon_park_used_dark},
            {R.drawable.icon_shopping_menu, R.drawable.icon_shopping_unselect_dark, R.drawable.icon_shopping_select_dark, R.drawable.icon_shopping_used_dark},
            {R.drawable.icon_phone_menu, R.drawable.icon_phone_unselect_dark, R.drawable.icon_phone_select_dark, R.drawable.icon_phone_used_dark},
            {R.drawable.icon_game_menu, R.drawable.icon_game_unselect_dark, R.drawable.icon_game_select_dark, R.drawable.icon_game_used_dark},
            {R.drawable.icon_art_menu, R.drawable.icon_art_unselect_dark, R.drawable.icon_art_select_dark, R.drawable.icon_art_used_dark},
            {R.drawable.icon_drink_menu, R.drawable.icon_drink_unselect_dark, R.drawable.icon_drink_select_dark, R.drawable.icon_drink_used_dark},
            {R.drawable.icon_cake_menu, R.drawable.icon_cake_unselect_dark, R.drawable.icon_cake_select_dark, R.drawable.icon_cake_used_dark},
            {R.drawable.icon_car_menu, R.drawable.icon_car_unselect_dark, R.drawable.icon_car_select_dark, R.drawable.icon_car_used_dark},
            {R.drawable.icon_plane_menu, R.drawable.icon_plane_unselect_dark, R.drawable.icon_plane_select_dark, R.drawable.icon_plane_used_dark},
            {R.drawable.icon_swim_menu, R.drawable.icon_swim_unselect_dark, R.drawable.icon_swim_select_dark, R.drawable.icon_swim_used_dark},
            {R.drawable.icon_star_menu, R.drawable.icon_star_unselect_dark, R.drawable.icon_star_select_dark, R.drawable.icon_star_used_dark},
            {R.drawable.icon_heart_menu, R.drawable.icon_heart_unselect_dark, R.drawable.icon_heart_select_dark, R.drawable.icon_heart_used_dark},
            {R.drawable.icon_camera_menu, R.drawable.icon_camera_unselect_dark, R.drawable.icon_camera_select_dark, R.drawable.icon_camera_used_dark},
            {R.drawable.icon_flower_menu, R.drawable.icon_flower_unselect_dark, R.drawable.icon_flower_select_dark, R.drawable.icon_flower_used_dark},
    };

    static int colorMappingTable[][] = {
            {R.drawable.icon_color_red_select, R.drawable.icon_color_red_unselect, R.drawable.icon_color_red_used, R.drawable.background_gradient_red},
            {R.drawable.icon_color_orange_select, R.drawable.icon_color_orange_unselect, R.drawable.icon_color_orange_used, R.drawable.background_gradient_orange},
            {R.drawable.icon_color_yellow_select, R.drawable.icon_color_yellow_unselect, R.drawable.icon_color_yellow_used, R.drawable.background_gradient_yellow},
            {R.drawable.icon_color_green_select, R.drawable.icon_color_green_unselect, R.drawable.icon_color_green_used, R.drawable.background_gradient_green},
            {R.drawable.icon_color_blue_select, R.drawable.icon_color_blue_unselect, R.drawable.icon_color_blue_used, R.drawable.background_gradient_blue},
            {R.drawable.icon_color_purple_select, R.drawable.icon_color_purple_unselect, R.drawable.icon_color_purple_used, R.drawable.background_gradient_purple},
            {R.drawable.icon_color_pink_select, R.drawable.icon_color_pink_unselect, R.drawable.icon_color_pink_used, R.drawable.background_gradient_pink},
            {R.drawable.icon_color_teal_select, R.drawable.icon_color_teal_unselect, R.drawable.icon_color_teal_used, R.drawable.background_gradient_teal},
            {R.drawable.icon_color_navy_select, R.drawable.icon_color_navy_unselect, R.drawable.icon_color_navy_used, R.drawable.background_gradient_navy},
            {R.drawable.icon_color_brown_select, R.drawable.icon_color_brown_unselect, R.drawable.icon_color_brown_used, R.drawable.background_gradient_brown},
            {R.drawable.icon_color_coral_select, R.drawable.icon_color_coral_unselect, R.drawable.icon_color_coral_used, R.drawable.background_gradient_coral},
            {R.drawable.icon_color_amber_select, R.drawable.icon_color_amber_unselect, R.drawable.icon_color_amber_used, R.drawable.background_gradient_amber},
            {R.drawable.icon_color_lime_select, R.drawable.icon_color_lime_unselect, R.drawable.icon_color_lime_used, R.drawable.background_gradient_lime},
            {R.drawable.icon_color_cyan_select, R.drawable.icon_color_cyan_unselect, R.drawable.icon_color_cyan_used, R.drawable.background_gradient_cyan},
            {R.drawable.icon_color_indigo_select, R.drawable.icon_color_indigo_unselect, R.drawable.icon_color_indigo_used, R.drawable.background_gradient_indigo},
            {R.drawable.icon_color_rose_select, R.drawable.icon_color_rose_unselect, R.drawable.icon_color_rose_used, R.drawable.background_gradient_rose},
            {R.drawable.icon_color_skyblue_select, R.drawable.icon_color_skyblue_unselect, R.drawable.icon_color_skyblue_used, R.drawable.background_gradient_skyblue},
            {R.drawable.icon_color_sage_select, R.drawable.icon_color_sage_unselect, R.drawable.icon_color_sage_used, R.drawable.background_gradient_sage},
            {R.drawable.icon_color_gold_select, R.drawable.icon_color_gold_unselect, R.drawable.icon_color_gold_used, R.drawable.background_gradient_gold},
            {R.drawable.icon_color_lavender_select, R.drawable.icon_color_lavender_unselect, R.drawable.icon_color_lavender_used, R.drawable.background_gradient_lavender},
            {R.drawable.icon_color_mint_select, R.drawable.icon_color_mint_unselect, R.drawable.icon_color_mint_used, R.drawable.background_gradient_mint},
            {R.drawable.icon_color_slate_select, R.drawable.icon_color_slate_unselect, R.drawable.icon_color_slate_used, R.drawable.background_gradient_slate},
            {R.drawable.icon_color_peach_select, R.drawable.icon_color_peach_unselect, R.drawable.icon_color_peach_used, R.drawable.background_gradient_peach},
            {R.drawable.icon_color_violet_select, R.drawable.icon_color_violet_unselect, R.drawable.icon_color_violet_used, R.drawable.background_gradient_violet},
            {R.drawable.icon_color_cherry_select, R.drawable.icon_color_cherry_unselect, R.drawable.icon_color_cherry_used, R.drawable.background_gradient_cherry},
            {R.drawable.icon_color_forest_select, R.drawable.icon_color_forest_unselect, R.drawable.icon_color_forest_used, R.drawable.background_gradient_forest},
            {R.drawable.icon_color_ocean_select, R.drawable.icon_color_ocean_unselect, R.drawable.icon_color_ocean_used, R.drawable.background_gradient_ocean},
            {R.drawable.icon_color_rust_select, R.drawable.icon_color_rust_unselect, R.drawable.icon_color_rust_used, R.drawable.background_gradient_rust},
            {R.drawable.icon_color_plum_select, R.drawable.icon_color_plum_unselect, R.drawable.icon_color_plum_used, R.drawable.background_gradient_plum},
            {R.drawable.icon_color_caramel_select, R.drawable.icon_color_caramel_unselect, R.drawable.icon_color_caramel_used, R.drawable.background_gradient_caramel},
    };

    public static int getCategoryIconResourceId(int type, int state) {
        return iconMappingTable[type][state];
    }

    public static int getCategoryColorResourceId(int type, int state) {
        return colorMappingTable[type][state];
    }
}
