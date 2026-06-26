package cn.edu.guet.cms3;

import java.util.*;

public class RandomPicker {
    public static void main(String[] args) {
        // 从签到表中提取的所有人名
        String[] names = {
            "陆正刚", "周鑫", "李波", "欧钰", "李冰", "杨洁晨", "陈少波", "孙成孟",
            "杨天寿", "张丰耀", "夏盛雨", "董从法", "吴宗煌", "盘皓", "廖崇兴", "吴必豪",
            "刘天", "陈小敏", "农政涛", "潘伟羽", "罗思雨", "覃桂灵", "陈最雄", "程观金",
            "姜世辉", "李俊军", "刘星宇", "陈辉寿", "刘炳林", "刘俊杰", "麦志明", "韦理威",
            "莫富安", "李禹顺", "梁炫烨", "罗进幸", "冯思明", "刘健星", "刘荣昆", "许伟光",
            "覃冰冰", "刘易天", "周烨", "徐枝林", "王龙宇", "邓国栋", "李剑生", "张业宏",
            "黄平志", "扈振洋"
        };
        
        // 创建随机数生成器
        Random random = new Random();
        
        // 确保数组中有至少2个人
        if (names.length < 2) {
            System.out.println("人员数量不足2人，无法抽取！");
            return;
        }
        
        // 随机抽取第一个人
        int firstIndex = random.nextInt(names.length);
        String firstPerson = names[firstIndex];
        
        // 随机抽取第二个人（确保不重复）
        int secondIndex;
        do {
            secondIndex = random.nextInt(names.length);
        } while (secondIndex == firstIndex);
        String secondPerson = names[secondIndex];
        
        // 输出结果
        System.out.println("随机抽取的2位人员是：");
        System.out.println("1. " + firstPerson);
        System.out.println("2. " + secondPerson);
    }
}