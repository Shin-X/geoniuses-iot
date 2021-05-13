package com.geoniuses.core.utils;

import io.netty.buffer.ByteBuf;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 井盖报文解析类
 * Created by admin on 2019/4/19.
 */
public class ParserWellCoverUtil {
    private static final DecimalFormat df = new DecimalFormat("0.00");
//    public static Map<String,Object> getDataByForm(String str,Map<String,Object> map){
//
//        List<Map<String,Object>> resultList = new ArrayList<>();
//        Map<String,Object> resultMap = new TreeMap<>();
//        resultList.add(resultMap);
//
//        for (String key : map.keySet()) {
//            Map data = (Map) map.get(key);
//            switch(data.get("type").toString()){
//                case "enum":
//                    String enumData = getEnumData(data, str);
//                    resultMap.put(key,enumData);
//                    continue;
//                case "calculate":
//                    String calculateData = getCalculateData(data, str);
//                    resultMap.put(key,calculateData);
//                    continue;
//                case "parser":
//                    String parserData = getParserData(data, str);
//                    resultMap.put(key,parserData);
//                    continue;
//                case "binary":
//                    Map<String, Object> binaryData = getBinaryData(data, str);
//                    resultMap.putAll(binaryData);
//                    continue;
//                case "symbol":
//                    String symbolData = getSymbolData(resultMap, data, str);
//                    resultMap.put(key,symbolData);
//                    continue;
//                case "crc":
//                    boolean crcData = getCrcData(data, str);
//                    resultMap.put(key,crcData);
//                    continue;
//                default:
//                    continue;
//            }
//
//        }
//        return resultMap;
//    }

    /**
     * type为parser的运算
     * @param data
     * @param str
     * @return
     */
//    private static String getParserData(Map data, String str) {
//        String parser = null;
//        List<String> use = (List<String>) data.get("use");
//        String s = use.get(0);
//        Map<String,Object> localtion = (Map) data.get("location");
////        List<Integer> localtion1 = (List<Integer>) localtion.get("localtion1");
//        for (String loc : localtion.keySet()) {
//            List<Integer> o = (List<Integer>) localtion.get(loc);
//            String substring = str.substring(o.get(0), o.get(1));
//            parser = String.valueOf(Long.valueOf(substring, 16));
//        }
//        return parser;
//    }


    /**
     * type为enum的运算
     * @param data
     * @param str
     * @return
     */
//    private static String getEnumData(Map data, String str) {
//        String result = null;
//        Map localtion = (Map) data.get("location");
//        List<Integer> localtion1 = (List<Integer>) localtion.get("location1");
//        String substring = str.substring(localtion1.get(0), localtion1.get(1));
//        Map<String,Object> stepMap = new TreeMap<>();
//        int i = Integer.parseInt(substring, 16);
//        stepMap.put("location1",i);
//        Map<String,Object> anEnum = (Map) data.get("enum");
//        for (String s : anEnum.keySet()) {
//            if (!s.contains("-")){ //如果不包含说明不是一个范围，直接取值即可
//                if (i == Integer.parseInt(s)){
//                    result = anEnum.get(s).toString();
//                    return result;
//                }
//            }else {
//                Map<String,Object> o = (Map) anEnum.get(s);
//                Map calculate = (Map) o.get("calculate");
//                putStepMap(calculate,stepMap);
//
//                result = ((TreeMap<String, Object>) stepMap).lastEntry().getValue().toString();
//            }
//        }
//
//        return result;
//    }
//
//
//    /**
//     * type 类型为binary的运算
//     * @param data
//     * @param str
//     * @return
//     * @throws IOException
//     */
//    private static Map<String,Object> getBinaryData(Map data, String str) {
//
//        Map<String,Object> binary = (Map) data.get("binary");
//        Map location = (Map) data.get("location");
//        List location1 = (List) location.get("location1");
//        String substring = str.substring(Integer.parseInt(location1.get(0).toString()), Integer.parseInt(location1.get(1).toString()));
//        int parseInt = Integer.parseInt(substring, 16);
//
//        Map<String,Object> stepMap = new TreeMap<String, Object>();
//        stepMap.put("location1",parseInt);
//        Map<String,Object> resultMap = new TreeMap<>();
//        String binaryNum = Integer.toBinaryString(parseInt);
//        while (binaryNum.length() < 8){
//            binaryNum =  "0" + binaryNum;
//        }
//        if (binary.size() >0){
//            for (String s : binary.keySet()) {
//                Map o = (Map) binary.get(s);
//                Map anEnum = (Map) o.get("enum");
//
//                List loca1 = (List) o.get("location1");
//                if (loca1.size() > 1){
//                    String substring1 = binaryNum.substring(Integer.parseInt(loca1.get(0).toString()), Integer.parseInt(loca1.get(1).toString()));
//                    if (anEnum.containsKey(substring1)){
//                        resultMap.put(s,anEnum.get(substring1));
//                    }
//                }else {
//                    String c = String.valueOf(binaryNum.charAt(Integer.parseInt(loca1.get(0).toString()))); //取出二进制中的位数
//                    if (anEnum.containsKey(c)){
//                        try {
//                            Map o1 = (Map) anEnum.get(c);   //c有可能是固定值，强转可能会报错
//                            Map<String,Object> calculate = (Map) o1.get("calculate");
//                            putStepMap(calculate,stepMap);
//                            resultMap.put(s,((TreeMap<String, Object>) stepMap).lastEntry().getValue().toString());
//                        } catch (ClassCastException e) {
//                            resultMap.put(s,anEnum.get(c));
//                        }
//                    }
//                }
//
//            }
//        }
//
//        return resultMap;
//
//    }
//
//    /**
//     * type为calculate的运算
//     * @param data
//     * @param str
//     * @return
//     */
//    private static String getCalculateData(Map data, String str) {
//
//        Map<String,List> location = (Map<String, List>) data.get("location");
//        Map<String,Object> stepMap = new TreeMap<>();
//        for (String s : location.keySet()) {
//            //将location中的数据位根据解析规则计算出数值
//            stepMap.put(s,Integer.parseInt(str.substring(Integer.parseInt(location.get(s).get(0).toString()),Integer.parseInt(location.get(s).get(1).toString())),16));
//        }
//        Map<String,Object> calculate = (Map) data.get("calculate");
//        putStepMap(calculate,stepMap);
//        //TODO 计算的值，改为Double，集控器电压需要保留两位小数
//        String value = ((TreeMap<String, Object>) stepMap).lastEntry().getValue().toString();
//        return value;
//    }
//
//
//    /**
//     * type 为Symbol的运算
//     * @param resultMap
//     * @param data
//     * @param str
//     * @return
//     */
//    private static String getSymbolData(Map resultMap, Map data, String str) {
//        Map<String,Object> symbol = (Map) data.get("symbol");
//        Map location = (Map) data.get("location");
//        List location1 = (List) location.get("location1");
//        String substring = str.substring(Integer.parseInt(location1.get(0).toString()), Integer.parseInt(location1.get(1).toString()));
//        int parseInt = Integer.parseInt(substring, 16);
//        Map<String,Object> stepMap = new TreeMap<>();
//        stepMap.putAll(resultMap);
//        stepMap.put("location1",parseInt);
//        List use = (List) data.get("use");
//        if (resultMap.containsKey(use.get(0))){
////            Integer o = Integer.parseInt(resultMap.get(use.get(0)).toString());
//            Double o = Double.parseDouble(resultMap.get(use.get(0)).toString());
//            for (String sKey: symbol.keySet()) {
//                if (o > Integer.parseInt(symbol.get("value").toString()) && sKey.equals(">")){
//                    Map o1 = (Map) symbol.get(sKey);
//                    Map<String,Object> calculate = (Map) o1.get("calculate");
//                    putStepMap(calculate,stepMap);
//                }else if(o >= Integer.parseInt(symbol.get("value").toString()) && sKey.equals(">=")){
//                    Map o1 = (Map) symbol.get(sKey);
//                    Map<String,Object> calculate = (Map) o1.get("calculate");
//                    putStepMap(calculate,stepMap);
//                }else if(o < Integer.parseInt(symbol.get("value").toString()) && sKey.equals("<")){
//                    Map o1 = (Map) symbol.get(sKey);
//                    Map<String,Object> calculate = (Map) o1.get("calculate");
//                    putStepMap(calculate,stepMap);
//                }else if(o <= Integer.parseInt(symbol.get("value").toString()) && sKey.equals("<=")){
//                    Map o1 = (Map) symbol.get(sKey);
//                    Map<String,Object> calculate = (Map) o1.get("calculate");
//                    putStepMap(calculate,stepMap);
//                }else {
//
//                }
//            }
//        }
//        return ((TreeMap<String, Object>) stepMap).lastEntry().getValue().toString();
//    }
//
//
//    /**
//     * type为crc的运算 校验和
//     * @param data
//     * @param str
//     * @return
//     */
//    private static boolean getCrcData(Map data, String str) {
//        Map<String,Object> location = (Map) data.get("location");
//
//        List location1 = (List) location.get("location1");
//        List location2 = (List) location.get("location2");
//
//        String substring = str.substring(Integer.parseInt(location1.get(0).toString()), Integer.parseInt(location1.get(1).toString()));
//        String checksum = CheckUtil.makeChecksum(substring);
//        String sum = str.substring(Integer.parseInt(location2.get(0).toString()), Integer.parseInt(location2.get(1).toString()));
//
//        boolean bool = false;
//        if (checksum.equalsIgnoreCase(sum)){
//            bool = true;
//        }
//        return bool;
//    }
//
//
//    /**
//     *
//     * @param calculateMap 需要运算的map
//     * @param stepMap 运算后的最终map
//     */
//    private static void putStepMap(Map<String,Object> calculateMap, Map<String,Object> stepMap) {
////      oKey 运算步骤的每一个key （step1，step2）
////        for (String oKey : calculateMap.keySet()) {
////            Map step = (Map) calculateMap.get(oKey);
////            List use = (List) step.get("use");
////            if (stepMap.containsKey(use.get(0).toString()) && !stepMap.containsKey(step.get("value").toString())){    //在运算步骤中可能use为step已计算的值，或者value为已计算的值
////                int loc = Integer.parseInt(stepMap.get(use.get(0).toString()).toString());
////                int value = Integer.parseInt(step.get("value").toString());
////                Integer stepData = getStepData(step.get("formula").toString(), loc, value);
////                stepMap.put(oKey,stepData);
////            }else if (stepMap.containsKey(use.get(0).toString()) && stepMap.containsKey(step.get("value").toString())){
////                //use和value中都为已经出的值
////                int loc = Integer.parseInt(stepMap.get(use.get(0).toString()).toString());
////                int value = Integer.parseInt(stepMap.get(step.get("value").toString()).toString());
////                Integer stepData = getStepData(step.get("formula").toString(), loc, value);
////                stepMap.put(oKey,stepData);
////            }else if (stepMap.containsKey(step.get("value").toString()) && !stepMap.containsKey(use.get(0).toString())){
////                //value为已经计算的值
////                int value = Integer.parseInt(stepMap.get(step.get("value").toString()).toString());
////                int loc = Integer.parseInt(use.get(0).toString());
////                Integer stepData = getStepData(step.get("formula").toString(), loc, value);
////                stepMap.put(oKey,stepData);
////            }else {
////                //都不是已经计算的值，都是数字
////                int loc = Integer.parseInt(use.get(0).toString());
////                int value = Integer.parseInt(step.get("value").toString());
////                Integer stepData = getStepData(step.get("formula").toString(), loc, value);
////                stepMap.put(oKey,stepData);
////            }
////        }
//        for (String oKey : calculateMap.keySet()) {
//            Map step = (Map) calculateMap.get(oKey);
//            List use = (List) step.get("use");
//            if (stepMap.containsKey(use.get(0).toString()) && !stepMap.containsKey(step.get("value").toString())){    //在运算步骤中可能use为step已计算的值，或者value为已计算的值
//                double loc = Double.parseDouble(stepMap.get(use.get(0).toString()).toString());
//                double value = Double.parseDouble(step.get("value").toString());
//                Double stepData = getStepData(step.get("formula").toString(), loc, value);
//                stepMap.put(oKey,stepData);
//            }else if (stepMap.containsKey(use.get(0).toString()) && stepMap.containsKey(step.get("value").toString())){
//                //use和value中都为已经出的值
//                double loc = Double.parseDouble(stepMap.get(use.get(0).toString()).toString());
//                double value = Double.parseDouble(stepMap.get(step.get("value").toString()).toString());
//                Double stepData = getStepData(step.get("formula").toString(), loc, value);
//                stepMap.put(oKey,stepData);
//            }else if (stepMap.containsKey(step.get("value").toString()) && !stepMap.containsKey(use.get(0).toString())){
//                //value为已经计算的值
//                double value = Double.parseDouble(stepMap.get(step.get("value").toString()).toString());
//                double loc = Double.parseDouble(use.get(0).toString());
//                Double stepData = getStepData(step.get("formula").toString(), loc, value);
//                stepMap.put(oKey,stepData);
//            }else {
//                //都不是已经计算的值，都是数字
//                double loc = Double.parseDouble(use.get(0).toString());
//                double value = Double.parseDouble(step.get("value").toString());
//                Double stepData = getStepData(step.get("formula").toString(), loc, value);
//                stepMap.put(oKey,stepData);
//            }
//        }
//    }
//
//
//    /**
//     * 运算符的运算
//     * @param formula
//     * @param loc
//     * @param value
//     * @return
//     */
//    private static Double getStepData(String formula, Double loc, Double value) {
//        switch (formula){
//            case "+":
//                loc += value;
//                break;
//            case  "-":
//                loc -= value;
//                break;
//            case "*":
//                loc *= value;
//                break;
//            case "/":
//                loc /= value;
//                break;
//            default:
//                break;
//        }
//        return loc;
//    }

    /**
     * e0	包头
     * 01  协议版本
     * 00 2f 02 7c 3f 41  网关号
     * 00 00 00 59  触发器编号
     * 00 00 	保留位两位
     * 01  	 0是集控器报道，1是触发器数据
     * 00 		集控器信号值	 （需要运算）
     * 0010  	集控器电压值	 （需要运算）
     * 4f 		触发器信号强度	 （需要运算）
     * 20		触发器信噪比	 （需要运算）
     * 08		随机数
     * 05     	转成二进制
     * {0 0 0 0 01 0 1}
     * 【数据包类型：1 信号 0报警，
     * 错误标志： 1 错误 0 正常，
     * 工作模式： 1 test 0 working，
     * 连接状态： 1 离线 0在线，
     * 数据包重复[11] Rerserved [10] Third [01] Second [00] First，
     * 触发器电量标志位：1低电量 0是正常，
     * 报警状态 1 报警 0正常】
     * f4   crc校验
     * 0a	 包尾
     *
     * @param byteBuf
     * @return
     */
    public static Map<String, Object> parser(ByteBuf byteBuf) {
        //包头
        int head = byteBuf.readUnsignedByte();
        //协议版本
        int version = byteBuf.readByte();

//        网关号 集控器编号
        byte[] bytes = new byte[6];
        byteBuf.readBytes(bytes);
//        String s = ByteArrayUtil.bytes2HexStr(bytes);
        long gateWayNo = Long.valueOf(ByteArrayUtil.bytes2HexStr(bytes), 16);

        //触发器编号
        long sensorNo = byteBuf.readUnsignedInt();

        //保留位 两位
        byteBuf.skipBytes(2);

        byte b = byteBuf.readByte();
        String type = null;
        if (b == 0x01) {
            type = "触发器数据";
        } else {
            type = "集控器数据";
        }
        int sdd1 = byteBuf.readUnsignedByte();
        int controllerSignalStrength;
        if (sdd1 == 0) {
            //这种情况下，信号强度设置为无穷小
            controllerSignalStrength = -120;
        } else if (sdd1 == 1) {
            controllerSignalStrength = -111;
        } else if (sdd1 >= 2 && sdd1 <= 30) {
            controllerSignalStrength = -calSignalStrength(51, 109, 2, 30, sdd1);
        } else if (sdd1 == 31) {
            controllerSignalStrength = -51;
        } else if (sdd1 == 99) {
            //这种情况下，信号强度设置为无穷小
            controllerSignalStrength = -120;
        } else if (sdd1 == 100) {
            controllerSignalStrength = -116;
        } else if (sdd1 == 101) {
            controllerSignalStrength = -115;
        } else if (sdd1 >= 102 && sdd1 <= 109) {
            controllerSignalStrength = -calSignalStrength(26, 114, 102, 109, sdd1);
        } else if (sdd1 == 191) {
            controllerSignalStrength = -25;
        } else if (sdd1 == 199) {
            //这种情况下，信号强度设置为无穷小
            controllerSignalStrength = -120; //0
        } else if (sdd1 >= 100 && sdd1 <= 199) {
            //controllerSignalStrength = controllerSignalStrength + "TD-SCDMA扩展"
            //这种情况下，信号强度设置为无穷小
            controllerSignalStrength = -120;
        } else {
            controllerSignalStrength = -120;
        }

        int msbBattery = byteBuf.readUnsignedByte();
        int lsbBattery = byteBuf.readUnsignedByte();

        //集控器电压
        String voltage = df.format((float) (msbBattery * 256 + lsbBattery) / 100);

        //触发器信号强度
        int tsnr1 = byteBuf.readUnsignedByte();
        //触发器信噪比
        int trssi = byteBuf.readUnsignedByte();
        int sensorSNR = trssi / 4;

        int sensorSignalStrength;

        if (sensorSNR > 0) {
            sensorSignalStrength = tsnr1 - 164;
        } else {
            sensorSignalStrength = tsnr1 - 164 + sensorSNR / 4;
        }

        //随机数
        int random = byteBuf.readUnsignedByte();

        int sdj = byteBuf.readUnsignedByte();
        int errorFlag, workingMode, connStatus, sensorPower, alarmStatus;
        String pkgRetry = null;
        String pkgType = null;

        //数据包类型：1 信号 0报警
        if (sdj >> 7 == 0) {
            pkgType = "Alarm";
        } else {
            pkgType = "Sign";
        }

        //报警状态 1 报警 0正常
        if ((sdj & 0x01) == 1) {
            alarmStatus = 1;
        } else {
            alarmStatus = 0;
        }

        //触发器电量标志位：1低电量 0是正常
        if ((sdj >> 1 & 0x01) == 1) {
            sensorPower = 1;
        } else {
            sensorPower = 0;
        }
        //数据包重复[11] Rerserved [10] Third [01] Second [00] First，
        if ((sdj >> 2 & 0x03) == 3) {
            pkgRetry = "Rerserved";
        } else if ((sdj >> 2 & 0x02) == 2) {
            pkgRetry = "Third";
        } else if ((sdj >> 2 & 0x01) == 1) {
            pkgRetry = "Second";
        } else if ((sdj >> 2 & 0x00) == 0) {
            pkgRetry = "First";
        }
        //连接状态： 1 离线 0在线
        if ((sdj >> 4 & 0x01) == 1) {
            connStatus = 1;
        } else {
            connStatus = 0;
        }
        //工作模式： 1 test 0 working
        if ((sdj >> 5 & 0x01) == 1) {
            workingMode = 1;
        } else {
            workingMode = 0;
        }
        //错误标志： 1 错误 0 正常
        if ((sdj >> 6 & 0x01) == 1) {
            errorFlag = 1;
        } else {
            errorFlag = 0;
        }

        byteBuf.skipBytes(byteBuf.readableBytes());
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sensorNo", sensorNo);
        resultMap.put("19", alarmStatus);
        resultMap.put("18", sensorPower);
        resultMap.put("17", pkgRetry);
        resultMap.put("16", connStatus);
        resultMap.put("15", workingMode);
        resultMap.put("14", errorFlag);
        resultMap.put("13", pkgType);

        resultMap.put("12", random);
        resultMap.put("11", sensorSignalStrength);
        resultMap.put("10", sensorSNR);
        resultMap.put("9", voltage);
        resultMap.put("8", controllerSignalStrength);
        resultMap.put("7", type);
        resultMap.put("3", gateWayNo);
        resultMap.put("2", version);
        resultMap.put("1", head);

        return resultMap;
    }

    /**
     * @param lowerValue 信号强度的低值
     * @param upperValue 信号强度的高值
     * @param lowerParam 参数范围的低值
     * @param upperParam 参数范围的高值
     * @param num        参数具体值
     * @return
     */
    private static int calSignalStrength(int lowerValue, int upperValue, int lowerParam, int upperParam, int num) {
//        int perInterval = (upperValue - lowerValue).toDouble / (upperParam - lowerParam);
        int perInterval = (upperValue - lowerValue) / (upperParam - lowerParam);
        int size = num - lowerParam;
        if (num == upperParam) {
            return upperValue;
        }
        if (num == lowerParam) {
            return lowerValue;
        }
        int result = lowerValue + size * perInterval;
        int round = Math.round(result);
        return round;
    }

    public static void main(String[] args) {
        String s = "12345678";
        char c = s.charAt(1);
        System.out.println("c = " + c);
    }
}


