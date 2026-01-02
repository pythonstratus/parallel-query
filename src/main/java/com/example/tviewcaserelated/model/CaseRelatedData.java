package com.example.tviewcaserelated.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * Data Transfer Object for Tviewcaserelated query results.
 * Maps all 97 columns from the optimized SQL query.
 * 
 * <p>Column categories:</p>
 * <ul>
 *   <li>Identity fields (ROID, SEID, TIN, TINSID)</li>
 *   <li>Taxpayer information (TP, address fields)</li>
 *   <li>Risk and indicators</li>
 *   <li>Date fields</li>
 *   <li>Case codes and grades</li>
 *   <li>Financial amounts</li>
 *   <li>Various status indicators</li>
 * </ul>
 */
public class CaseRelatedData {
    
    // ========================================================================
    // Identity Fields
    // ========================================================================
    private String roid;
    private String seid;
    private String tin;
    private String tintt;
    private String tinfs;
    private Long tinsid;
    
    // ========================================================================
    // Taxpayer Information
    // ========================================================================
    private String tp;
    private String tp2;
    private String street;
    private String street2;
    private String city;
    private String state;
    private Integer zipcde;
    private String tpctrl;
    
    // ========================================================================
    // Risk Fields
    // ========================================================================
    private Integer cRisk;
    private Integer hRisk;
    private Integer risk;
    private String arank;
    
    // ========================================================================
    // Case Indicators
    // ========================================================================
    private String cCaseind;
    private String hCaseind;
    private String caseind;
    private String contactcd;
    
    // ========================================================================
    // Date Fields
    // ========================================================================
    private Date extrdt;
    private Date rptdt;
    private Date closedt;
    private Date dtDod;
    private Date xxdt;
    private Date initdt;
    private Date dtOa;
    private Date dtPoa;
    private Date pickdt;
    private Date assnfld;
    private Date assncff;
    private Date assnro;
    private Date priorAssgmntActDt;
    
    // ========================================================================
    // Case Codes
    // ========================================================================
    private String cCasecode;
    private String casecode;
    private String hCasecode;
    private String cSubcode;
    private String subcode;
    private String hSubcode;
    private String timecode;
    private String timedesc;
    private String timedef;
    
    // ========================================================================
    // Grade Fields
    // ========================================================================
    private Integer cGrade;
    private Integer hGrade;
    private Integer casegrade;
    
    // ========================================================================
    // Hours Fields
    // ========================================================================
    private BigDecimal hours;
    private BigDecimal fldhrs;
    private BigDecimal emphrs;
    private BigDecimal hrs;
    private BigDecimal tothrs;
    
    // ========================================================================
    // Tour/Program Fields
    // ========================================================================
    private String bodcd;
    private String tour;
    private String prgname1;
    private String prgname2;
    
    // ========================================================================
    // Financial Fields
    // ========================================================================
    private BigDecimal totassd;
    private BigDecimal bal94114;
    private BigDecimal bal941;
    private BigDecimal agiAmt;
    private BigDecimal totIrpInc;
    private BigDecimal totIncDelqYr;
    private BigDecimal priorYrRetAgiAmt;
    private BigDecimal txperTxpyrAmt;
    
    // ========================================================================
    // Count Fields
    // ========================================================================
    private Integer cnt94114;
    private Integer cnt941;
    private Integer tdiCnt941;
    private Integer tdacnt;
    private Integer tdicnt;
    private Integer modcnt;
    private Integer priorAssgmntNum;
    
    // ========================================================================
    // Status Indicators
    // ========================================================================
    private Integer statind;
    private Integer ind941;
    private String formattedInd941;
    private String hinfind;
    private String ageind;
    private Integer cauind;
    private Integer pyrent;
    private Integer pyrind;
    private String fatcaind;
    private String fedconind;
    private String fedempind;
    private String irsempind;
    private String l903;
    private Integer lfiind;
    private String llcind;
    private String rptind;
    private String theftind;
    private String inspcind;
    private String oicaccyr;
    private String ldind;
    
    // ========================================================================
    // Other Fields
    // ========================================================================
    private String naicscd;
    private String ccnipselectcd;
    private String assnque;
    private String dvictcd;
    private String qpickind;
    private BigDecimal emptouch;
    private BigDecimal lsttouch;
    private BigDecimal tottouch;
    private String proid;
    private String selcode;
    private String status;
    private Integer dispcd;
    private String cc;

    // ========================================================================
    // Constructors
    // ========================================================================
    
    /**
     * Default constructor.
     */
    public CaseRelatedData() {
    }

    // ========================================================================
    // Getters and Setters - Identity Fields
    // ========================================================================
    
    public String getRoid() {
        return roid;
    }

    public void setRoid(String roid) {
        this.roid = roid;
    }

    public String getSeid() {
        return seid;
    }

    public void setSeid(String seid) {
        this.seid = seid;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getTintt() {
        return tintt;
    }

    public void setTintt(String tintt) {
        this.tintt = tintt;
    }

    public String getTinfs() {
        return tinfs;
    }

    public void setTinfs(String tinfs) {
        this.tinfs = tinfs;
    }

    public Long getTinsid() {
        return tinsid;
    }

    public void setTinsid(Long tinsid) {
        this.tinsid = tinsid;
    }

    // ========================================================================
    // Getters and Setters - Taxpayer Information
    // ========================================================================
    
    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    public String getTp2() {
        return tp2;
    }

    public void setTp2(String tp2) {
        this.tp2 = tp2;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getZipcde() {
        return zipcde;
    }

    public void setZipcde(Integer zipcde) {
        this.zipcde = zipcde;
    }

    public String getTpctrl() {
        return tpctrl;
    }

    public void setTpctrl(String tpctrl) {
        this.tpctrl = tpctrl;
    }

    // ========================================================================
    // Getters and Setters - Risk Fields
    // ========================================================================
    
    public Integer getCRisk() {
        return cRisk;
    }

    public void setCRisk(Integer cRisk) {
        this.cRisk = cRisk;
    }

    public Integer getHRisk() {
        return hRisk;
    }

    public void setHRisk(Integer hRisk) {
        this.hRisk = hRisk;
    }

    public Integer getRisk() {
        return risk;
    }

    public void setRisk(Integer risk) {
        this.risk = risk;
    }

    public String getArank() {
        return arank;
    }

    public void setArank(String arank) {
        this.arank = arank;
    }

    // ========================================================================
    // Getters and Setters - Case Indicators
    // ========================================================================
    
    public String getCCaseind() {
        return cCaseind;
    }

    public void setCCaseind(String cCaseind) {
        this.cCaseind = cCaseind;
    }

    public String getHCaseind() {
        return hCaseind;
    }

    public void setHCaseind(String hCaseind) {
        this.hCaseind = hCaseind;
    }

    public String getCaseind() {
        return caseind;
    }

    public void setCaseind(String caseind) {
        this.caseind = caseind;
    }

    public String getContactcd() {
        return contactcd;
    }

    public void setContactcd(String contactcd) {
        this.contactcd = contactcd;
    }

    // ========================================================================
    // Getters and Setters - Date Fields
    // ========================================================================
    
    public Date getExtrdt() {
        return extrdt;
    }

    public void setExtrdt(Date extrdt) {
        this.extrdt = extrdt;
    }

    public Date getRptdt() {
        return rptdt;
    }

    public void setRptdt(Date rptdt) {
        this.rptdt = rptdt;
    }

    public Date getClosedt() {
        return closedt;
    }

    public void setClosedt(Date closedt) {
        this.closedt = closedt;
    }

    public Date getDtDod() {
        return dtDod;
    }

    public void setDtDod(Date dtDod) {
        this.dtDod = dtDod;
    }

    public Date getXxdt() {
        return xxdt;
    }

    public void setXxdt(Date xxdt) {
        this.xxdt = xxdt;
    }

    public Date getInitdt() {
        return initdt;
    }

    public void setInitdt(Date initdt) {
        this.initdt = initdt;
    }

    public Date getDtOa() {
        return dtOa;
    }

    public void setDtOa(Date dtOa) {
        this.dtOa = dtOa;
    }

    public Date getDtPoa() {
        return dtPoa;
    }

    public void setDtPoa(Date dtPoa) {
        this.dtPoa = dtPoa;
    }

    public Date getPickdt() {
        return pickdt;
    }

    public void setPickdt(Date pickdt) {
        this.pickdt = pickdt;
    }

    public Date getAssnfld() {
        return assnfld;
    }

    public void setAssnfld(Date assnfld) {
        this.assnfld = assnfld;
    }

    public Date getAssncff() {
        return assncff;
    }

    public void setAssncff(Date assncff) {
        this.assncff = assncff;
    }

    public Date getAssnro() {
        return assnro;
    }

    public void setAssnro(Date assnro) {
        this.assnro = assnro;
    }

    public Date getPriorAssgmntActDt() {
        return priorAssgmntActDt;
    }

    public void setPriorAssgmntActDt(Date priorAssgmntActDt) {
        this.priorAssgmntActDt = priorAssgmntActDt;
    }

    // ========================================================================
    // Getters and Setters - Case Codes
    // ========================================================================
    
    public String getCCasecode() {
        return cCasecode;
    }

    public void setCCasecode(String cCasecode) {
        this.cCasecode = cCasecode;
    }

    public String getCasecode() {
        return casecode;
    }

    public void setCasecode(String casecode) {
        this.casecode = casecode;
    }

    public String getHCasecode() {
        return hCasecode;
    }

    public void setHCasecode(String hCasecode) {
        this.hCasecode = hCasecode;
    }

    public String getCSubcode() {
        return cSubcode;
    }

    public void setCSubcode(String cSubcode) {
        this.cSubcode = cSubcode;
    }

    public String getSubcode() {
        return subcode;
    }

    public void setSubcode(String subcode) {
        this.subcode = subcode;
    }

    public String getHSubcode() {
        return hSubcode;
    }

    public void setHSubcode(String hSubcode) {
        this.hSubcode = hSubcode;
    }

    public String getTimecode() {
        return timecode;
    }

    public void setTimecode(String timecode) {
        this.timecode = timecode;
    }

    public String getTimedesc() {
        return timedesc;
    }

    public void setTimedesc(String timedesc) {
        this.timedesc = timedesc;
    }

    public String getTimedef() {
        return timedef;
    }

    public void setTimedef(String timedef) {
        this.timedef = timedef;
    }

    // ========================================================================
    // Getters and Setters - Grade Fields
    // ========================================================================
    
    public Integer getCGrade() {
        return cGrade;
    }

    public void setCGrade(Integer cGrade) {
        this.cGrade = cGrade;
    }

    public Integer getHGrade() {
        return hGrade;
    }

    public void setHGrade(Integer hGrade) {
        this.hGrade = hGrade;
    }

    public Integer getCasegrade() {
        return casegrade;
    }

    public void setCasegrade(Integer casegrade) {
        this.casegrade = casegrade;
    }

    // ========================================================================
    // Getters and Setters - Hours Fields
    // ========================================================================
    
    public BigDecimal getHours() {
        return hours;
    }

    public void setHours(BigDecimal hours) {
        this.hours = hours;
    }

    public BigDecimal getFldhrs() {
        return fldhrs;
    }

    public void setFldhrs(BigDecimal fldhrs) {
        this.fldhrs = fldhrs;
    }

    public BigDecimal getEmphrs() {
        return emphrs;
    }

    public void setEmphrs(BigDecimal emphrs) {
        this.emphrs = emphrs;
    }

    public BigDecimal getHrs() {
        return hrs;
    }

    public void setHrs(BigDecimal hrs) {
        this.hrs = hrs;
    }

    public BigDecimal getTothrs() {
        return tothrs;
    }

    public void setTothrs(BigDecimal tothrs) {
        this.tothrs = tothrs;
    }

    // ========================================================================
    // Getters and Setters - Tour/Program Fields
    // ========================================================================
    
    public String getBodcd() {
        return bodcd;
    }

    public void setBodcd(String bodcd) {
        this.bodcd = bodcd;
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public String getPrgname1() {
        return prgname1;
    }

    public void setPrgname1(String prgname1) {
        this.prgname1 = prgname1;
    }

    public String getPrgname2() {
        return prgname2;
    }

    public void setPrgname2(String prgname2) {
        this.prgname2 = prgname2;
    }

    // ========================================================================
    // Getters and Setters - Financial Fields
    // ========================================================================
    
    public BigDecimal getTotassd() {
        return totassd;
    }

    public void setTotassd(BigDecimal totassd) {
        this.totassd = totassd;
    }

    public BigDecimal getBal94114() {
        return bal94114;
    }

    public void setBal94114(BigDecimal bal94114) {
        this.bal94114 = bal94114;
    }

    public BigDecimal getBal941() {
        return bal941;
    }

    public void setBal941(BigDecimal bal941) {
        this.bal941 = bal941;
    }

    public BigDecimal getAgiAmt() {
        return agiAmt;
    }

    public void setAgiAmt(BigDecimal agiAmt) {
        this.agiAmt = agiAmt;
    }

    public BigDecimal getTotIrpInc() {
        return totIrpInc;
    }

    public void setTotIrpInc(BigDecimal totIrpInc) {
        this.totIrpInc = totIrpInc;
    }

    public BigDecimal getTotIncDelqYr() {
        return totIncDelqYr;
    }

    public void setTotIncDelqYr(BigDecimal totIncDelqYr) {
        this.totIncDelqYr = totIncDelqYr;
    }

    public BigDecimal getPriorYrRetAgiAmt() {
        return priorYrRetAgiAmt;
    }

    public void setPriorYrRetAgiAmt(BigDecimal priorYrRetAgiAmt) {
        this.priorYrRetAgiAmt = priorYrRetAgiAmt;
    }

    public BigDecimal getTxperTxpyrAmt() {
        return txperTxpyrAmt;
    }

    public void setTxperTxpyrAmt(BigDecimal txperTxpyrAmt) {
        this.txperTxpyrAmt = txperTxpyrAmt;
    }

    // ========================================================================
    // Getters and Setters - Count Fields
    // ========================================================================
    
    public Integer getCnt94114() {
        return cnt94114;
    }

    public void setCnt94114(Integer cnt94114) {
        this.cnt94114 = cnt94114;
    }

    public Integer getCnt941() {
        return cnt941;
    }

    public void setCnt941(Integer cnt941) {
        this.cnt941 = cnt941;
    }

    public Integer getTdiCnt941() {
        return tdiCnt941;
    }

    public void setTdiCnt941(Integer tdiCnt941) {
        this.tdiCnt941 = tdiCnt941;
    }

    public Integer getTdacnt() {
        return tdacnt;
    }

    public void setTdacnt(Integer tdacnt) {
        this.tdacnt = tdacnt;
    }

    public Integer getTdicnt() {
        return tdicnt;
    }

    public void setTdicnt(Integer tdicnt) {
        this.tdicnt = tdicnt;
    }

    public Integer getModcnt() {
        return modcnt;
    }

    public void setModcnt(Integer modcnt) {
        this.modcnt = modcnt;
    }

    public Integer getPriorAssgmntNum() {
        return priorAssgmntNum;
    }

    public void setPriorAssgmntNum(Integer priorAssgmntNum) {
        this.priorAssgmntNum = priorAssgmntNum;
    }

    // ========================================================================
    // Getters and Setters - Status Indicators
    // ========================================================================
    
    public Integer getStatind() {
        return statind;
    }

    public void setStatind(Integer statind) {
        this.statind = statind;
    }

    public Integer getInd941() {
        return ind941;
    }

    public void setInd941(Integer ind941) {
        this.ind941 = ind941;
    }

    public String getFormattedInd941() {
        return formattedInd941;
    }

    public void setFormattedInd941(String formattedInd941) {
        this.formattedInd941 = formattedInd941;
    }

    public String getHinfind() {
        return hinfind;
    }

    public void setHinfind(String hinfind) {
        this.hinfind = hinfind;
    }

    public String getAgeind() {
        return ageind;
    }

    public void setAgeind(String ageind) {
        this.ageind = ageind;
    }

    public Integer getCauind() {
        return cauind;
    }

    public void setCauind(Integer cauind) {
        this.cauind = cauind;
    }

    public Integer getPyrent() {
        return pyrent;
    }

    public void setPyrent(Integer pyrent) {
        this.pyrent = pyrent;
    }

    public Integer getPyrind() {
        return pyrind;
    }

    public void setPyrind(Integer pyrind) {
        this.pyrind = pyrind;
    }

    public String getFatcaind() {
        return fatcaind;
    }

    public void setFatcaind(String fatcaind) {
        this.fatcaind = fatcaind;
    }

    public String getFedconind() {
        return fedconind;
    }

    public void setFedconind(String fedconind) {
        this.fedconind = fedconind;
    }

    public String getFedempind() {
        return fedempind;
    }

    public void setFedempind(String fedempind) {
        this.fedempind = fedempind;
    }

    public String getIrsempind() {
        return irsempind;
    }

    public void setIrsempind(String irsempind) {
        this.irsempind = irsempind;
    }

    public String getL903() {
        return l903;
    }

    public void setL903(String l903) {
        this.l903 = l903;
    }

    public Integer getLfiind() {
        return lfiind;
    }

    public void setLfiind(Integer lfiind) {
        this.lfiind = lfiind;
    }

    public String getLlcind() {
        return llcind;
    }

    public void setLlcind(String llcind) {
        this.llcind = llcind;
    }

    public String getRptind() {
        return rptind;
    }

    public void setRptind(String rptind) {
        this.rptind = rptind;
    }

    public String getTheftind() {
        return theftind;
    }

    public void setTheftind(String theftind) {
        this.theftind = theftind;
    }

    public String getInspcind() {
        return inspcind;
    }

    public void setInspcind(String inspcind) {
        this.inspcind = inspcind;
    }

    public String getOicaccyr() {
        return oicaccyr;
    }

    public void setOicaccyr(String oicaccyr) {
        this.oicaccyr = oicaccyr;
    }

    public String getLdind() {
        return ldind;
    }

    public void setLdind(String ldind) {
        this.ldind = ldind;
    }

    // ========================================================================
    // Getters and Setters - Other Fields
    // ========================================================================
    
    public String getNaicscd() {
        return naicscd;
    }

    public void setNaicscd(String naicscd) {
        this.naicscd = naicscd;
    }

    public String getCcnipselectcd() {
        return ccnipselectcd;
    }

    public void setCcnipselectcd(String ccnipselectcd) {
        this.ccnipselectcd = ccnipselectcd;
    }

    public String getAssnque() {
        return assnque;
    }

    public void setAssnque(String assnque) {
        this.assnque = assnque;
    }

    public String getDvictcd() {
        return dvictcd;
    }

    public void setDvictcd(String dvictcd) {
        this.dvictcd = dvictcd;
    }

    public String getQpickind() {
        return qpickind;
    }

    public void setQpickind(String qpickind) {
        this.qpickind = qpickind;
    }

    public BigDecimal getEmptouch() {
        return emptouch;
    }

    public void setEmptouch(BigDecimal emptouch) {
        this.emptouch = emptouch;
    }

    public BigDecimal getLsttouch() {
        return lsttouch;
    }

    public void setLsttouch(BigDecimal lsttouch) {
        this.lsttouch = lsttouch;
    }

    public BigDecimal getTottouch() {
        return tottouch;
    }

    public void setTottouch(BigDecimal tottouch) {
        this.tottouch = tottouch;
    }

    public String getProid() {
        return proid;
    }

    public void setProid(String proid) {
        this.proid = proid;
    }

    public String getSelcode() {
        return selcode;
    }

    public void setSelcode(String selcode) {
        this.selcode = selcode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDispcd() {
        return dispcd;
    }

    public void setDispcd(Integer dispcd) {
        this.dispcd = dispcd;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    // ========================================================================
    // Object Methods
    // ========================================================================
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaseRelatedData that = (CaseRelatedData) o;
        return Objects.equals(tinsid, that.tinsid) &&
               Objects.equals(tin, that.tin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tinsid, tin);
    }

    @Override
    public String toString() {
        return "CaseRelatedData{" +
                "tinsid=" + tinsid +
                ", tin='" + (tin != null ? "***" + tin.substring(Math.max(0, tin.length() - 4)) : null) + '\'' +
                ", grade=" + cGrade +
                ", status='" + status + '\'' +
                ", casecode='" + casecode + '\'' +
                ", totassd=" + totassd +
                '}';
    }
    
    /**
     * Get a detailed string representation for debugging.
     */
    public String toDetailedString() {
        return "CaseRelatedData{" +
                "\n  tinsid=" + tinsid +
                "\n  roid='" + roid + '\'' +
                "\n  seid='" + seid + '\'' +
                "\n  grade=" + cGrade +
                "\n  status='" + status + '\'' +
                "\n  casecode='" + casecode + '\'' +
                "\n  subcode='" + subcode + '\'' +
                "\n  totassd=" + totassd +
                "\n  bal941=" + bal941 +
                "\n  city='" + city + '\'' +
                "\n  state='" + state + '\'' +
                "\n}";
    }
}
