package com.xiaocai.qunaer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.csvreader.CsvReader;
import com.xiaocai.qunaer.util.POIUtils;
import com.xiaocai.qunaer.vo.RepaymentVo4Qunaer;



public class CheckBill4Qunaer {
    private static final String[] format = new String[] {"瀹炴敹鏈噾"};
	public static void main(String args[]) throws Exception{
		Map<String,List<RepaymentVo4Qunaer>> repaymentMap=importRepaymentAndRefundExcel();
		checkBill(repaymentMap);
	}

    private static Map<String, List<RepaymentVo4Qunaer>> importRepaymentAndRefundExcel() throws Exception {
		InputStream ips=new FileInputStream("E:\\1.xlsx");
		
		Workbook workbook = null;
        Sheet sheet = null;
        try {
            workbook = new XSSFWorkbook(ips);
            sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                throw new Exception("");
            }
        } catch (IOException ioe) {
            throw new IOException("", ioe);
        }
        Map<String, List<RepaymentVo4Qunaer>> repaymentMap=new HashMap<String, List<RepaymentVo4Qunaer>>();
        List<Map<String, Object>> readDataList = POIUtils.initExcelData(
            sheet, format, false);
        System.out.println("readDataListSize:"+readDataList.size());
        int i=0;
        for (Map<String, Object> dataMap : readDataList) {
             String loanNo=(String)dataMap.get("test");

             BigDecimal totalamout=new BigDecimal((String)dataMap.get("test"));
            
             BigDecimal capital=new BigDecimal((String)dataMap.get("test"));
            
             BigDecimal fee=new BigDecimal((String)dataMap.get("test"));
            
             BigDecimal fine=new BigDecimal((String)dataMap.get("test"));
             
             RepaymentVo4Qunaer repayment=new RepaymentVo4Qunaer();
             
             repayment.setCapital(capital);
             repayment.setFee(fee);
             repayment.setFine(fine);
             repayment.setLoanNo(loanNo);
             repayment.setTotalamout(totalamout);
             List<RepaymentVo4Qunaer> list=repaymentMap.get(loanNo)!=null?repaymentMap.get(loanNo):new ArrayList<RepaymentVo4Qunaer>();
             list.add(repayment);
             repaymentMap.put(loanNo, list);
        }
        
        System.out.println("repaymentMapSize:"+repaymentMap.size());
		return repaymentMap;
		
	}
    
    private static void checkBill(Map<String, List<RepaymentVo4Qunaer>> repaymentMap) {
        checkRepyment(repaymentMap);
       checkRefund(repaymentMap);
        
    }

    private static void checkRepyment(
            Map<String, List<RepaymentVo4Qunaer>> repaymentMap) {
        String filePath="E:\\download\\2016-05-";
        String endName="_YRDLOAN_repaymentInfo.txt";
        int j=0;
        int m=0;
        for(int i=1;i<31;i++){
            String middleName=i<10?"0"+i:""+i;
            String fileName=filePath+middleName+endName;
            CsvReader reader = null;
            try{
                
                InputStream inputStream=new FileInputStream(fileName);
                reader = new CsvReader(inputStream, '|', Charset.forName("utf-8"));
                reader.readHeaders();
                int n=0;
                while (reader.readRecord()) {
                    String[] obj=reader.getValues();
                    String loanNo=obj[1];
                    if(loanNo.length()<4){
                        continue;
                    }
                    n++;
                    List<RepaymentVo4Qunaer> list=repaymentMap.get(loanNo);
                    if(list==null||list.size()<1){
                        System.out.println("鏈壘鍒板搴旂殑key:"+obj[1]);
                        return;
                    }
                    int listSize=list.size();
                    int k=0;
                    int q=0;
                    for(RepaymentVo4Qunaer repayment:list){
                        q++;
                         if(repayment.getTotalamout().equals(new BigDecimal(obj[6]))
                                 &&repayment.getCapital().equals(new BigDecimal(obj[10]))
                                 &&repayment.getFee().equals(new BigDecimal(obj[11]))
                                 &&repayment.getFine().equals(new BigDecimal(obj[12]))){
                             //System.out.println(loanNo);
                             
                             list.remove(repayment);
                             j++;
                             break;
                         }
                         if(k==0&&q==listSize){
                             System.out.println("鏈壘鍒板搴旂殑鍊硷紝瀵硅处鏂囦欢涓庡簱閲屾暟鎹笉瀵瑰簲:"+loanNo);
                         }
                    }
                    //System.out.println(list.size());
                }
                System.out.println("n="+n);
                System.out.println("瀹屾垚:"+fileName);
                m=m+n;
            }catch(Exception e){
                System.out.println(ExceptionUtils.getStackTrace(e));
                System.out.println("i="+i);
                System.out.println("j="+j);
                System.out.println("m="+m);
                return;
            }
            
        }
        System.out.println("j="+j);
        
    }

    private static void checkRefund(Map<String, List<RepaymentVo4Qunaer>> repaymentMap) {
        String filePath="E:\\x\\download\\2016-05-";
        String endName="_YRDLOAN_refundInfo.txt";
        int j=0;
        int m=0;
        for(int i=1;i<31;i++){
            String middleName=i<10?"0"+i:""+i;
            String fileName=filePath+middleName+endName;
            CsvReader reader = null;
            try{
                
                InputStream inputStream=new FileInputStream(fileName);
                reader = new CsvReader(inputStream, '|', Charset.forName("utf-8"));
                reader.readHeaders();
                int n=0;
                while (reader.readRecord()) {
                    String[] obj=reader.getValues();
                    String loanNo=obj[1];
                    if(loanNo.length()<4){
                        continue;
                    }
                    n++;
                    List<RepaymentVo4Qunaer> list=repaymentMap.get(loanNo);
                    if(list==null||list.size()<1){
                        System.out.println("鏈壘鍒板搴旂殑key:"+obj[1]);
                        return;
                    }
                    int k=0;
                    int q=0;
                    int listSize=list.size();
                    for(RepaymentVo4Qunaer repayment:list){
                        q++;
                         if(repayment.getTotalamout().equals(new BigDecimal(obj[4]))
                                 &&repayment.getCapital().equals(new BigDecimal(obj[8]))
                                 &&repayment.getFee().equals(new BigDecimal(obj[9]))
                                 &&repayment.getFine().equals(new BigDecimal(obj[10]))){
                             //System.out.println(loanNo);
                             
                             list.remove(repayment);
                             j++;
                             k++;
                             break;
                         }
                         if(k==0&&q==listSize){
                             System.out.println("鏈壘鍒板搴旂殑鍊硷紝瀵硅处鏂囦欢涓庡簱閲屾暟鎹笉瀵瑰簲:"+loanNo);
                         }
                    }
                    //System.out.println(list.size());
                }
                System.out.println("n="+n);
                System.out.println("瀹屾垚:"+fileName);
                m=m+n;
            }catch(Exception e){
                System.out.println(ExceptionUtils.getStackTrace(e));
                System.out.println("i="+i);
                System.out.println("j="+j);
                System.out.println("m="+m);
                return;
            }
            
        }
        System.out.println("j="+j);
        
    }
}