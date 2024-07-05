// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    static int _100ms = 120000, _10s = 12000000;
    static PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
    //时间戳->时间数值
    static Map<Integer, Integer> period = new HashMap<>();
    public static Long transfer(String TimeStamp){
        String[] hhmmss = TimeStamp.substring(0, 8).split(":");
        String[] msms = TimeStamp.substring(9).split("\\.");
        Long second = 3600*Long.parseLong(hhmmss[0])+60*Long.parseLong(hhmmss[1])+Long.parseLong(hhmmss[2]);
        Long MicroSecond = second*1000000L+Long.parseLong(msms[0])*1000+Long.parseLong(msms[1]);
        return MicroSecond;
    }
    //返回的第一个参数：周期是否正确 第二个参数是：错误的index
    public static String[] check(List<String> TimeStream, int distance){
        double average_period = 0;
        String falsePoint = "-1";
        double MaxDelay = 0;
        for(int i=0; i<TimeStream.size()-1; i++){
            long TimeStamp1 = transfer(TimeStream.get(i));
            long TimeStamp2 = transfer(TimeStream.get(i+1));
            average_period = average_period+TimeStamp2-TimeStamp1;
            MaxDelay = Math.max(MaxDelay, TimeStamp2-TimeStamp1);
            if(TimeStamp2-TimeStamp1>distance){
                falsePoint = TimeStream.get(i);
            }
        }
        average_period = average_period/(TimeStream.size()-1);
        return average_period<=distance?new String[]{"1", falsePoint, average_period+"", MaxDelay+""}:new String[]{"0", falsePoint, average_period+"", MaxDelay+""};
    }


    public static void main(String[] args)throws IOException{
        //记录每个CanID能容忍的周期
        period.put(300, _100ms);
        period.put(301, _100ms);
        period.put(302, _10s);
        period.put(303, _10s);
        period.put(304, _10s);
        period.put(305, _10s);
        period.put(306, _10s);
        //读取文件
        BufferedReader in = new BufferedReader(new FileReader("D:\\util\\park_cam\\新建文件夹\\GCANTools\\a.txt"));
        in.readLine();
        String contentLine = in.readLine();
        Map<Integer, List<String>> log = new HashMap<>();
        while(contentLine!=null){
            //System.out.println(contentLine);
            String[] input = contentLine.split("\\s+");
            int CanID = Integer.parseInt(input[4]);
            if(!log.containsKey(CanID)) log.put(CanID, new ArrayList<>());
            log.get(CanID).add(input[2]);
            contentLine = in.readLine();
        }
        List<Integer> CorrectCanId = new ArrayList<>(), UncorrectCanId = new ArrayList<>();
        for(Map.Entry<Integer, List<String>> entry : log.entrySet()){
            String[] respond = check(entry.getValue(), period.get(entry.getKey()));
            if(respond[0].equals("1") && Double.parseDouble(respond[3])<period.get(entry.getKey())){
                CorrectCanId.add(entry.getKey());
                out.printf("正确的CanID: %d, 测量周期: %f ms, 最大延迟为 %f ms\n", entry.getKey(), Double.parseDouble(respond[2])/1000, Double.parseDouble(respond[3])/1000);
            }
            else{
                UncorrectCanId.add(entry.getKey());
                out.printf("错误的CanID: %d, 错误的时间点: %s, 测量周期为: %f ms, 最大延迟为: %f ms\n", entry.getKey(), respond[1], Double.parseDouble(respond[2])/1000, Double.parseDouble(respond[3])/1000);
            }
        }
        out.printf("结论：正确的CanID数量共 %d 个，错误的CanID数量共 %d 个\n", CorrectCanId.size(), UncorrectCanId.size());
        out.flush();
        out.close();
    }
}