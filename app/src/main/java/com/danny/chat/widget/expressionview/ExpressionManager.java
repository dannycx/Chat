package com.danny.chat.widget.expressionview;

import android.util.SparseIntArray;

import com.danny.chat.R;


/**
 * Created by danny on 3/28/18.
 */
public class ExpressionManager {
    private static ExpressionManager ourInstance;
    private SparseIntArray faceMap = null;

    private ExpressionManager() {
        faceMap = new SparseIntArray();
        faceMap.append(0, R.mipmap.face_0);
        faceMap.append(1, R.mipmap.face_1);
        faceMap.append(2, R.mipmap.face_2);
        faceMap.append(3, R.mipmap.face_3);
        faceMap.append(4, R.mipmap.face_4);
        faceMap.append(5, R.mipmap.face_5);
        faceMap.append(6, R.mipmap.face_6);
        faceMap.append(7, R.mipmap.face_7);
        faceMap.append(8, R.mipmap.face_8);
        faceMap.append(9, R.mipmap.face_9);
        faceMap.append(10, R.mipmap.face_10);
        faceMap.append(11, R.mipmap.face_11);
        faceMap.append(12, R.mipmap.face_12);
        faceMap.append(13, R.mipmap.face_13);
        faceMap.append(14, R.mipmap.face_14);
        faceMap.append(15, R.mipmap.face_15);
        faceMap.append(16, R.mipmap.face_16);
        faceMap.append(17, R.mipmap.face_17);
        faceMap.append(18, R.mipmap.face_18);
        faceMap.append(19, R.mipmap.face_19);
        faceMap.append(20, R.mipmap.face_20);
        faceMap.append(21, R.mipmap.face_21);
        faceMap.append(22, R.mipmap.face_22);
        faceMap.append(23, R.mipmap.face_23);
        faceMap.append(24, R.mipmap.face_24);
        faceMap.append(25, R.mipmap.face_25);
        faceMap.append(26, R.mipmap.face_26);
        faceMap.append(27, R.mipmap.face_27);
        faceMap.append(28, R.mipmap.face_28);
        faceMap.append(29, R.mipmap.face_29);
        faceMap.append(30, R.mipmap.face_30);
        faceMap.append(31, R.mipmap.face_31);
        faceMap.append(32, R.mipmap.face_32);
        faceMap.append(33, R.mipmap.face_33);
        faceMap.append(34, R.mipmap.face_34);
        faceMap.append(35, R.mipmap.face_35);
        faceMap.append(36, R.mipmap.face_36);
        faceMap.append(37, R.mipmap.face_37);
        faceMap.append(38, R.mipmap.face_38);
        faceMap.append(39, R.mipmap.face_39);
        faceMap.append(40, R.mipmap.face_40);
        faceMap.append(41, R.mipmap.face_41);
        faceMap.append(42, R.mipmap.face_42);
        faceMap.append(43, R.mipmap.face_43);
        faceMap.append(44, R.mipmap.face_44);
        faceMap.append(45, R.mipmap.face_45);
        faceMap.append(46, R.mipmap.face_46);
        faceMap.append(47, R.mipmap.face_47);
        faceMap.append(48, R.mipmap.face_48);
        faceMap.append(49, R.mipmap.face_49);
        faceMap.append(50, R.mipmap.face_50);
        faceMap.append(51, R.mipmap.face_51);
        faceMap.append(52, R.mipmap.face_52);
        faceMap.append(53, R.mipmap.face_53);
        faceMap.append(54, R.mipmap.face_54);
        faceMap.append(55, R.mipmap.face_55);
        faceMap.append(56, R.mipmap.face_56);
        faceMap.append(57, R.mipmap.face_57);
        faceMap.append(58, R.mipmap.face_58);
        faceMap.append(59, R.mipmap.face_59);
        faceMap.append(60, R.mipmap.face_60);
        faceMap.append(61, R.mipmap.face_61);
        faceMap.append(62, R.mipmap.face_62);
        faceMap.append(63, R.mipmap.face_63);
        faceMap.append(64, R.mipmap.face_64);
        faceMap.append(65, R.mipmap.face_65);
        faceMap.append(66, R.mipmap.face_66);
        faceMap.append(67, R.mipmap.face_67);
        faceMap.append(68, R.mipmap.face_68);
        faceMap.append(69, R.mipmap.face_69);
        faceMap.append(70, R.mipmap.face_70);
        faceMap.append(71, R.mipmap.face_71);
        faceMap.append(72, R.mipmap.face_72);
        faceMap.append(73, R.mipmap.face_73);
        faceMap.append(74, R.mipmap.face_74);
        faceMap.append(75, R.mipmap.face_75);
        faceMap.append(76, R.mipmap.face_76);
        faceMap.append(77, R.mipmap.face_77);
        faceMap.append(78, R.mipmap.face_78);
        faceMap.append(79, R.mipmap.face_79);
        faceMap.append(80, R.mipmap.face_80);
        faceMap.append(81, R.mipmap.face_81);
        faceMap.append(82, R.mipmap.face_82);
        faceMap.append(83, R.mipmap.face_83);
        faceMap.append(84, R.mipmap.face_84);
        faceMap.append(85, R.mipmap.face_85);
        faceMap.append(86, R.mipmap.face_86);
        faceMap.append(87, R.mipmap.face_87);
        faceMap.append(88, R.mipmap.face_88);
        faceMap.append(89, R.mipmap.face_89);
        faceMap.append(90, R.mipmap.face_90);
        faceMap.append(91, R.mipmap.face_91);
        faceMap.append(92, R.mipmap.face_92);
        faceMap.append(93, R.mipmap.face_93);
        faceMap.append(94, R.mipmap.face_94);
        faceMap.append(95, R.mipmap.face_95);
        faceMap.append(96, R.mipmap.face_96);
        faceMap.append(97, R.mipmap.face_97);
        faceMap.append(98, R.mipmap.face_98);
        faceMap.append(99, R.mipmap.face_99);
        faceMap.append(100, R.mipmap.face_100);
        faceMap.append(101, R.mipmap.face_101);
        faceMap.append(102, R.mipmap.face_102);
        faceMap.append(103, R.mipmap.face_103);
        faceMap.append(104, R.mipmap.face_104);
    }

    public static ExpressionManager getInstance() {
        if (ourInstance == null)
            ourInstance = new ExpressionManager();
        return ourInstance;
    }

    public int getExpression(int expressionNum) {
        return faceMap.get(expressionNum);
    }
}
