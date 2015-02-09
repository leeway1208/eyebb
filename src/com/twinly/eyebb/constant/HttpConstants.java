package com.twinly.eyebb.constant;

public class HttpConstants {

	public static final int CONNECT_TIMEOUT = 5000;
	/**
	 * 158.182.246.221 == test.eyebb.com (Testing)
	 * 158.182.8.221 == srv.eyebb.com (Production)
	 * 158.182.220.206 == test.eyebb.com (Testing)
	 * 158.182.246.223 == srv.eyebb.com (Production)
	 */
	public static final String SERVER_URL = "http://test.eyebb.com:8089/";
	//public static final String SERVER_URL = "http://158.182.220.206:8089/";
	//public static final String SERVER_URL = "http://158.182.246.223:8080/";
	public static final String HTTP_POST_RESPONSE_URL_NULL = "Url Null";
	public static final String HTTP_POST_RESPONSE_EXCEPTION = "Failed to connect to server";

	public static final String JSON_KEY_CHILD_ID = "childId";
	public static final String JSON_KEY_CHILD_NAME = "name";
	public static final String JSON_KEY_CHILD_ICON = "icon";

	public static final String LOGIN = "j_spring_security_check";
	public static final String JSON_KEY_USER = "guardian";
	public static final String JSON_KEY_USER_ID = "guardianId";
	public static final String JSON_KEY_USER_NAME = "name";
	public static final String JSON_KEY_USER_PHONE = "phoneNumber";
	public static final String JSON_KEY_USER_TYPE = "type";
	public static final String JSON_KEY_REGISTRATION_ID = "registrationId";

	public static final String GET_KINDERGARTEN_LIST = "kindergartenList";
	public static final String JSON_KEY_AREAS_INFO = "allLocationAreasInfo";
	public static final String JSON_KEY_AREAS_id = "areaId";
	public static final String JSON_KEY_KINDERGARTEN_NAME_EN = "name";
	public static final String JSON_KEY_KINDERGARTEN_NAME_TC = "nameTc";
	public static final String JSON_KEY_KINDERGARTEN_NAME_SC = "nameSc";

	//public static final String GET_CHILDREN_LIST = "reportService/api/childrenList";
	public static final String GET_CHILDREN_INFO_LIST = "reportService/api/childrenInfoList";
	public static final String JSON_KEY_CHILDREN_INFO = "childrenInfo";
	
	public static final String GET_CHILDREN_LOC_LIST = "reportService/api/childrenLocList";
	public static final String JSON_KEY_CHILDREN_BY_AREA = "childrenByArea";
	public static final String JSON_KEY_CHILDREN_BEAN = "childrenBean";
	
	public static final String JSON_KEY_CHILD_REL = "childRel";
	public static final String JSON_KEY_CHILD = "child";
	public static final String JSON_KEY_CHILD_RELATION = "relation";
	public static final String JSON_KEY_CHILD_MAC_ADDRESS = "macAddress";
	public static final String JSON_KEY_CHILD_LAST_APPEAR_TIME = "lastAppearTime";
	public static final String JSON_KEY_CHILD_LOC_ID = "locId";

	public static final String JSON_KEY_LOCATION_ALL = "allLocations";
	public static final String JSON_KEY_LOCATION_AREA = "area";
	public static final String JSON_KEY_LOCATION_AREA_ID = "areaId";
	public static final String JSON_KEY_LOCATION_AREA_NAME = "name";
	public static final String JSON_KEY_LOCATION_AREA_NAME_TC = "nameTc";
	public static final String JSON_KEY_LOCATION_AREA_NAME_SC = "nameSc";
	public static final String JSON_KEY_LOCATION_AREA_ICON = "icon";
	public static final String JSON_KEY_LOCATIONS = "locations";
	public static final String JSON_KEY_LOCATION_ID = "locationId";
	public static final String JSON_KEY_LOCATION_NAME = "locationName";
	public static final String JSON_KEY_LOCATION_NAME_SC = "nameSc";
	public static final String JSON_KEY_LOCATION_NAME_TC = "nameTc";
	public static final String JSON_KEY_LOCATION_TYPE = "type";
	public static final String JSON_KEY_LOCATION_ICON = "icon";

	public static final String JSON_KEY_PARENTS = "parents";
	public static final String JSON_KEY_PARENTS_PHONE = "phoneNumber";
	public static final String JSON_KEY_PARENTS_TYPE = "type";

	public static final String GET_REPORTS = "reportService/api/stat";
	public static final String JSON_KEY_REPORT_PERFORMANCE = "dailyAvgFigure";
	public static final String JSON_KEY_REPORT_PERFORMANCE_LOC_NAME = "locName";
	public static final String JSON_KEY_REPORT_PERFORMANCE_DAILY = "daily";
	public static final String JSON_KEY_REPORT_PERFORMANCE_AVERAGE = "average";
	public static final String JSON_KEY_REPORT_PERFORMANCE_LAST_UPDATE_TIME = "lastUpdateTime";

	public static final String JSON_KEY_REPORT_ACTIVITY_INFO = "activityInfos";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_TITLE = "title";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_TC = "titleTc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_TITLE_SC = "titleSc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_URL = "activity";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_URL_TC = "activityTc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_URL_SC = "activitySc";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_ICON = "icon";
	public static final String JSON_KEY_REPORT_ACTIVITY_INFO_DATE = "validUntil";

	public static final String GUEST_CHILDREN = "masterService/api/guestChildren";
	public static final String JSON_KEY_CHILDREN_QUOTA = "childrenQuota";
	public static final String JSON_KEY_WITH_ACCESS = "withAccess";
	public static final String JSON_KEY_TOTAL_QUOTA = "totalQuota";
	public static final String JSON_KEY_QUOTA_LEFT = "quotaLeft";

	public static final String AUTH_FIND_GUESTS = "masterService/api/searchMasterGuests";
	public static final String JSON_KEY_GUESTS = "guests";
	public static final String JSON_KEY_MASTERS = "masters";
	public static final String JSON_KEY_CHILDREN_BY_GUARDIAN = "chilrenByGuardian";

	public static final String GET_NOTICES = "reportService/api/notices";
	public static final String JSON_KEY_NOTICES = "notices";
	public static final String JSON_KEY_NOTICES_TITLE = "title";
	public static final String JSON_KEY_NOTICES_TITLE_TC = "titleTc";
	public static final String JSON_KEY_NOTICES_TITLE_SC = "titleSc";
	public static final String JSON_KEY_NOTICES_NOTICE = "notice";
	public static final String JSON_KEY_NOTICES_NOTICE_TC = "noticeTc";
	public static final String JSON_KEY_NOTICES_NOTICE_SC = "noticeSc";
	public static final String JSON_KEY_NOTICES_ICON = "icon";
	public static final String JSON_KEY_NOTICES_VALID_UNTIL = "validUntil";

	public static final String SEARCH_GUEST = "masterService/api/searchGuest";
	public static final String UPDATE_REGISTRATION_ID = "/accSetting/api/updateDeviceId";
	public static final String ACC_NAME_CHECK = "regService/api/accNameCheck";
	public static final String REG_PARENTS = "regService/api/regParents";
	public static final String CHILD_CHECKING = "masterService/api/childChecking";
	public static final String CHILD_GUA_REL = "masterService/api/regGuaChildRel";
	public static final String CHECK_BEACON = "masterService/api/checkBeacon";
	public static final String DEVICE_TO_CHILD = "masterService/api/persistBeaconChildRel";
	public static final String FEED_BACK = "reportService/api/feedbacks";
	public static final String CHECK_IF_CHILD_HAS_BEACON = "masterService/api/hasBeaconOrNot";
	public static final String UNBIND_CHILD_BEACON = "masterService/api/unbindChildBeacon";
	public static final String GRANT_GUESTS = "masterService/api/grantGuestAccess";
	public static final String LOGIN_INFO = "reportService/api/loginInfo";
	public static final String UPDATE_PASSWORD = "accSetting/api/updatePassword";
	public static final String RESET_PASSWORD = "regService/api/resetPassword";
	public static final String GET_MASTER_CHILDREN = "masterService/api/masterChildren";
	public static final String CHANGE_NICKNAME = "accSetting/api/changeNickname";
	public static final String REQUIRE_OR_GET_QR_CODE = "masterService/api/childMacAddressAvailable";

	public static final String SERVER_RETURN_true = "true";
	public static final String SERVER_RETURN_TRUE = "TRUE";
	public static final String SERVER_RETURN_false = "false";
	public static final String SERVER_RETURN_fALSE = "fALSE";
	public static final String SERVER_RETURN_USED = "USED";
	public static final String SERVER_RETURN_N = "N";
	public static final String SERVER_RETURN_Y = "Y";
	public static final String SERVER_RETURN_T = "T";
	public static final String SERVER_RETURN_F = "F";
	public static final String SERVER_RETURN_E = "E";
	public static final String SERVER_RETURN_WG = "WG";
	public static final String SERVER_RETURN_NC = "NC";

}
