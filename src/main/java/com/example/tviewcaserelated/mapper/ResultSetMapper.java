package com.example.tviewcaserelated.mapper;

import com.example.tviewcaserelated.model.CaseRelatedData;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps ResultSet rows to CaseRelatedData objects.
 * Handles null values and type conversions safely.
 */
public class ResultSetMapper {
    
    /**
     * Map a single ResultSet row to CaseRelatedData.
     *
     * @param rs The ResultSet positioned at a valid row
     * @return Populated CaseRelatedData object
     * @throws SQLException if database access error occurs
     */
    public CaseRelatedData mapRow(ResultSet rs) throws SQLException {
        CaseRelatedData data = new CaseRelatedData();
        
        // Identity fields
        data.setRoid(rs.getString("ROID"));
        data.setSeid(rs.getString("SEID"));
        data.setTin(rs.getString("TIN"));
        data.setTintt(rs.getString("TINTT"));
        data.setTinfs(rs.getString("TINFS"));
        data.setTinsid(getLongOrNull(rs, "TINSID"));
        
        // Taxpayer info
        data.setTp(rs.getString("TP"));
        data.setTp2(rs.getString("TP2"));
        data.setStreet(rs.getString("STREET"));
        data.setStreet2(rs.getString("STREET2"));
        data.setCity(rs.getString("CITY"));
        data.setState(rs.getString("STATE"));
        data.setZipcde(getIntOrNull(rs, "ZIPCDE"));
        data.setTpctrl(rs.getString("TPCTRL"));
        
        // Risk fields
        data.setCRisk(getIntOrNull(rs, "C_RISK"));
        data.setHRisk(getIntOrNull(rs, "H_RISK"));
        data.setRisk(getIntOrNull(rs, "RISK"));
        data.setArank(rs.getString("ARANK"));
        
        // Case indicators
        data.setCCaseind(rs.getString("C_CASEIND"));
        data.setHCaseind(rs.getString("H_CASEIND"));
        data.setCaseind(rs.getString("CASEIND"));
        data.setContactcd(rs.getString("CONTACTCD"));
        
        // Date fields
        data.setExtrdt(rs.getDate("EXTRDT"));
        data.setRptdt(rs.getDate("RPTDT"));
        data.setClosedt(rs.getDate("CLOSEDT"));
        data.setDtDod(rs.getDate("DT_DOD"));
        data.setXxdt(rs.getDate("XXDT"));
        data.setInitdt(rs.getDate("INITDT"));
        data.setDtOa(rs.getDate("DT_OA"));
        data.setDtPoa(rs.getDate("DT_POA"));
        data.setPickdt(rs.getDate("PICKDT"));
        data.setAssnfld(rs.getDate("ASSNFLD"));
        data.setAssncff(rs.getDate("ASSNCFF"));
        data.setAssnro(rs.getDate("ASSNRO"));
        data.setPriorAssgmntActDt(rs.getDate("PRIOR_ASSGMNT_ACT_DT"));
        
        // Case codes
        data.setCCasecode(rs.getString("C_CASECODE"));
        data.setCasecode(rs.getString("CASECODE"));
        data.setHCasecode(rs.getString("H_CASECODE"));
        data.setCSubcode(rs.getString("C_SUBCODE"));
        data.setSubcode(rs.getString("SUBCODE"));
        data.setHSubcode(rs.getString("H_SUBCODE"));
        data.setTimecode(rs.getString("TIMECODE"));
        data.setTimedesc(rs.getString("TIMEDESC"));
        data.setTimedef(rs.getString("TIMEDEF"));
        
        // Grade fields
        data.setCGrade(getIntOrNull(rs, "C_GRADE"));
        data.setHGrade(getIntOrNull(rs, "H_GRADE"));
        data.setCasegrade(getIntOrNull(rs, "CASEGRADE"));
        
        // Hours fields
        data.setHours(rs.getBigDecimal("HOURS"));
        data.setFldhrs(rs.getBigDecimal("FLDHRS"));
        data.setEmphrs(rs.getBigDecimal("EMPHRS"));
        data.setHrs(rs.getBigDecimal("HRS"));
        data.setTothrs(rs.getBigDecimal("TOTHRS"));
        
        // Tour/Program
        data.setBodcd(rs.getString("BODCD"));
        data.setTour(rs.getString("TOUR"));
        data.setPrgname1(rs.getString("PRGNAME1"));
        data.setPrgname2(rs.getString("PRGNAME2"));
        
        // Financial fields
        data.setTotassd(rs.getBigDecimal("TOTASSD"));
        data.setBal94114(rs.getBigDecimal("BAL_941_14"));
        data.setBal941(rs.getBigDecimal("BAL_941"));
        data.setAgiAmt(rs.getBigDecimal("AGI_AMT"));
        data.setTotIrpInc(rs.getBigDecimal("TOT_IRP_INC"));
        data.setTotIncDelqYr(rs.getBigDecimal("TOT_INC_DELQ_YR"));
        data.setPriorYrRetAgiAmt(rs.getBigDecimal("PRIOR_YR_RET_AGI_AMT"));
        data.setTxperTxpyrAmt(rs.getBigDecimal("TXPER_TXPYR_AMT"));
        
        // Count fields
        data.setCnt94114(getIntOrNull(rs, "CNT_941_14"));
        data.setCnt941(getIntOrNull(rs, "CNT_941"));
        data.setTdiCnt941(getIntOrNull(rs, "TDI_CNT_941"));
        data.setTdacnt(getIntOrNull(rs, "TDACNT"));
        data.setTdicnt(getIntOrNull(rs, "TDICNT"));
        data.setModcnt(getIntOrNull(rs, "MODCNT"));
        data.setPriorAssgmntNum(getIntOrNull(rs, "PRIOR_ASSGMNT_NUM"));
        
        // Status indicators
        data.setStatind(getIntOrNull(rs, "STATIND"));
        data.setInd941(getIntOrNull(rs, "IND_941"));
        data.setFormattedInd941(rs.getString("FORMATTED_IND_941"));
        data.setHinfind(rs.getString("HINFIND"));
        data.setAgeind(rs.getString("AGEIND"));
        data.setCauind(getIntOrNull(rs, "CAUIND"));
        data.setPyrent(getIntOrNull(rs, "PYRENT"));
        data.setPyrind(getIntOrNull(rs, "PYRIND"));
        data.setFatcaind(rs.getString("FATCAIND"));
        data.setFedconind(rs.getString("FEDCONIND"));
        data.setFedempind(rs.getString("FEDEMPIND"));
        data.setIrsempind(rs.getString("IRSEMPIND"));
        data.setL903(rs.getString("L903"));
        data.setLfiind(getIntOrNull(rs, "LFIIND"));
        data.setLlcind(rs.getString("LLCIND"));
        data.setRptind(rs.getString("RPTIND"));
        data.setTheftind(rs.getString("THEFTIND"));
        data.setInspcind(rs.getString("INSPCIND"));
        data.setOicaccyr(rs.getString("OICACCYR"));
        data.setLdind(rs.getString("LDIND"));
        
        // Other fields
        data.setNaicscd(rs.getString("NAICSCD"));
        data.setCcnipselectcd(rs.getString("CCNIPSELECTCD"));
        data.setAssnque(rs.getString("ASSNQUE"));
        data.setDvictcd(rs.getString("DVICTCD"));
        data.setQpickind(rs.getString("QPICKIND"));
        data.setEmptouch(rs.getBigDecimal("EMPTOUCH"));
        data.setLsttouch(rs.getBigDecimal("LSTTOUCH"));
        data.setTottouch(rs.getBigDecimal("TOTTOUCH"));
        data.setProid(rs.getString("PROID"));
        data.setSelcode(rs.getString("SELCODE"));
        data.setStatus(rs.getString("STATUS"));
        data.setDispcd(getIntOrNull(rs, "DISPCD"));
        data.setCc(rs.getString("CC"));
        
        return data;
    }
    
    /**
     * Get Long value handling SQL NULL.
     */
    private Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
    
    /**
     * Get Integer value handling SQL NULL.
     */
    private Integer getIntOrNull(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }
}
