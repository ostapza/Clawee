package services.utility;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class TimeService {
    public long getEpoch(int year, int month, int day) {
        LocalDateTime localDate = LocalDateTime.of(year, month, day, 00, 00, 00);
        return localDate.toEpochSecond(ZoneOffset.ofHours(2));
    }

    public long getEpoch(int year, int month, int day, int zoneHours) {
        LocalDateTime localDate = LocalDateTime.of(year, month, day, 00, 00, 00);
        return localDate.toEpochSecond(ZoneOffset.ofHours(zoneHours));
    }

    public String getDateFromEpoch(long epoch) {
        LocalDateTime localDate = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.ofHours(2));
        return localDate.format(DateTimeFormatter.ofPattern("dd-MM"));
    }
    public long getEpochDay(String date) {
        LocalDateTime localDate2 = LocalDateTime.parse(date.replace("/", "-") + "-00-00-00-+0200", DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss-Z"));
        return localDate2.toEpochSecond(ZoneOffset.ofHours(2));
    }

    public long getEpochMonth(String date) {
        LocalDateTime localDate2 = LocalDateTime.parse(date.replace("/", "-") + "-00-00-00-+0200", DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss-Z"));
        return localDate2.toEpochSecond(ZoneOffset.ofHours(0));
    }

    public int getDaysInMonth(int month){

        Month month1 = Month.of(month);


        return month1.length(false);
    }
    public Map<Integer, Integer> getWeekendDays(long epoch){
        LocalDateTime loc = LocalDateTime.ofEpochSecond(epoch,0, ZoneOffset.ofHours(0));
        if(loc.getDayOfMonth()!=1){
            epoch-=86400*(loc.getDayOfMonth()-1);
            loc = LocalDateTime.ofEpochSecond(epoch,0, ZoneOffset.ofHours(0));
        }

        YearMonth yearMonth = YearMonth.of(loc.getYear(), loc.getMonth());
        int startDay = loc.getDayOfWeek().getValue();
        int sunday = 0;
        int monday = 0;
        int tuesday = 0;
        int wednesday = 0;
        int thursday = 0;
        int friday = 0;
        int saturday = 0;
        for(int i = 0; i<yearMonth.lengthOfMonth();i++){
            switch(startDay) {
                case(1):
                    monday++;
                    break;
                case(2):
                    tuesday++;
                    break;
                case(3):
                    wednesday++;
                    break;
                case(4):
                    thursday++;
                    break;
                case(5):
                    friday++;
                    break;
                case(6):
                    saturday++;
                    break;
                case(7): {
                    sunday++;
                    startDay=0;
                    break;
                }
            }
            startDay++;
        }
        Map<Integer, Integer> weekendsCount = new HashMap<>();
        weekendsCount.put(0, sunday);
        weekendsCount.put(1,monday);
        weekendsCount.put(2,tuesday);
        weekendsCount.put(3,wednesday);
        weekendsCount.put(4,thursday);
        weekendsCount.put(5,friday);
        weekendsCount.put(6,saturday);
        return weekendsCount;
    }
    public Boolean inCurrentMonth(long epoch, long dayoff){
            LocalDateTime loc = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.ofHours(0));
            LocalDateTime dayof = LocalDateTime.ofEpochSecond(dayoff, 0, ZoneOffset.ofHours(0));
            return dayof.getMonth().equals(loc.getMonth());
    }
    public int daysInMonth(long epoch){
        LocalDateTime loc = LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.ofHours(0));
        return YearMonth.of(loc.getYear(),loc.getMonth()).lengthOfMonth();
    }
    public Map<Integer, Integer> getParamDays(long epoch){
        LocalDateTime loc = LocalDateTime.ofEpochSecond(epoch,0, ZoneOffset.ofHours(0));
        if(loc.getDayOfMonth()!=1){
            epoch-=86400*(loc.getDayOfMonth()-1);
            loc = LocalDateTime.ofEpochSecond(epoch,0, ZoneOffset.ofHours(0));
        }

        YearMonth yearMonth = YearMonth.of(loc.getYear(), loc.getMonth());
        int startDay = loc.getDayOfWeek().getValue();
        int sunday = 0;
        int monday = 0;
        int tuesday = 0;
        int wednesday = 0;
        int thursday = 0;
        int friday = 0;
        int saturday = 0;
        int lengOfMonth = yearMonth.lengthOfMonth();
        int nonTFullDays = lengOfMonth%7;
        int weeksInMonth = 0;
        if(7-startDay>=nonTFullDays){
            if(nonTFullDays==0)
            weeksInMonth = (lengOfMonth - nonTFullDays)/7;
            else
                weeksInMonth= (lengOfMonth - nonTFullDays)/7+1;
        }else{
            weeksInMonth = (lengOfMonth - nonTFullDays)/7 +2;
        }

        Map<Integer, Integer> weekendsCount = new HashMap<>();
        weekendsCount.put(0, sunday);
        weekendsCount.put(1,monday);
        weekendsCount.put(2,tuesday);
        weekendsCount.put(3,wednesday);
        weekendsCount.put(4,thursday);
        weekendsCount.put(5,friday);
        weekendsCount.put(6,saturday);
        return weekendsCount;
    }
}
