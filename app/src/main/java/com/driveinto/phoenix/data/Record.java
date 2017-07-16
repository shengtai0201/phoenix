package com.driveinto.phoenix.data;

import java.util.Date;

// 追蹤紀錄
public class Record {
    // 系統記錄時間
    private Date systemRecordTime;
    //    // 編號
//    private String serialNumber;
    private String customerId;

    // 年
    private Integer year;
    // 複診時間
    private Date referralTime;
    // 上胸圍
    private Integer upBust;
    // 下胸圍
    private Integer downBust;
    // 乳容量
    private Integer milkCapacity;
    // 胃(肚)圍
    private Integer bellyCircumference;
    // 腰圍
    private Integer waistCircumference;
    // 臀圍
    private Integer hipCircumference;
    // 左腿圍
    private Integer leftLegCircumference;
    // 右腿圍
    private Integer rightLegCircumference;
    // 設計組合
    private String designCombination;
    // 購買組合
    private String buyCombination;
    // 備註
    private String remark;
    // 皮膚狀況
    private SkinCondition[] skinConditions;
    // 膚質
    private SkinTexture[] skinTextures;
    // 保養建議
    private String maintenanceAdvice;
    // 保養程序
    private String maintenanceProcedure;
}
