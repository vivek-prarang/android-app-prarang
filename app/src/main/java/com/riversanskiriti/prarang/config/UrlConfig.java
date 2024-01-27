package com.riversanskiriti.prarang.config;

/**
 * Created by ARPIT on 19-03-2017.
 */

public class UrlConfig {

    //TEST SERVER
    private static String testServer = "https://www.sirrat.com/riverSanskiriti/";

    //LIVE SERVER
//    private static String productionServer = "https://apps.prarang.in/";

    // For Test Server in 2022
    private static String productionServer = "https://prarang.in/prapi/";

    // For Test Server in 2022
//    private static String productionServer = "https://sirrat.com/PrarangAdminDemo/PrarangApi/";

    //SERVER
    private static String server = productionServer;

    //STARTER
    public static String getCityReagion = server + "cityRegionListApi.php";
    public static String userLocation = server + "userLocation.php";

    //REG
    public static String login = server + "registrationResponse.php";
    public static String profilePic = server + "profilePic.php";

    //POST Older
//    public static String cityPost = server + "cityWiseChitti.php";
//    public static String regionPost = server + "regionWiseChitti.php";
//    public static String countryPost = server + "countryWiseChitti.php";

    //POST New
    public static String jaunpurCityPost = server + "jaunpurCityChitti.php";
    public static String lucknowCityPost = server + "LucknowCityChitti.php";
    public static String rampurCityPost = server + "RampurCityChitti.php";
    public static String meerutCityPost = server + "MeerutCityChitti.php";

    //LIKE & COMMENT
    public static String chittiLike = server + "chittiLike.php";
    public static String getChittiComment = server + "getChittiComment.php";
    public static String postChittiComment = server + "postChittiComment.php";

    //TAGS
    public static String tagcount = server + "tagcount.php";
    public static String taglist = server + "taglist.php";
    public static String tagWisePost = server + "tagWisePost.php";

    //OTHERS
    public static String chittiDetails = server + "ChittiDetails.php";

    //PLAYSTORE
    public static String playStoreURL = "https://play.google.com/store/apps/details";

//    -------------------------------- MIS  START-----------------------------------------

    // Code added by Pawan
    public static String appUsageURL = server + "appusage.php";

    public static String postShareUrl = server + "Share.php";

    public static String feedCountUrl = server + "FeedCount.php";

    public static String saveBankUrl = server + "SaveBank.php";
//    -------------------------------- MIS  END-----------------------------------------

//    -------------------------------- OTHER NEW API-----------------------------------------

    public static String jaunpurCityPostBankURL = server + "JaunpurBankList.php";
    public static String lucknowCityPostBankURL = server + "LucknowBankList.php";
    public static String rampurCityPostBankURL = server + "RampurBankList.php";
    public static String meerutCityPostBankURL = server + "MeerutBankList.php";
    public static String tagPostList = server + "tagpostlist.php";


//    -------------------------------- OTHER NEW API END-----------------------------------------

}
