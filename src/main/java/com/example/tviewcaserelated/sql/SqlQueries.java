package com.example.tviewcaserelated.sql;

/**
 * SQL query constants for Tviewcaserelated.
 * Contains the optimized SQL query with CTEs and JOINs.
 */
public final class SqlQueries {
    
    private SqlQueries() {
        // Prevent instantiation
    }
    
    /**
     * Optimized Tviewcaserelated SQL query.
     * 
     * <p>Optimizations applied:</p>
     * <ul>
     *   <li>Converted scalar subqueries to JOINs (SEID, TOUR, SELCODE)</li>
     *   <li>Replaced EXISTS with LEFT JOIN for entmod check</li>
     *   <li>Used CTEs to materialize intermediate results</li>
     *   <li>All columns have explicit table aliases</li>
     *   <li>Pre-filtered TIMETIN CTE reduces data volume early</li>
     * </ul>
     */
    public static final String TVIEWCASERELATED_QUERY = """
        WITH 
        -- Pre-filter the main tables to reduce data volume early
        filtered_timetin AS (
            SELECT /*+ MATERIALIZE */
                tt.*
            FROM TIMETIN tt
            WHERE tt.RPTDT > TO_DATE('01/01/1900', 'mm/dd/yyyy')
              AND case_org(tt.roid) = 'CF'
        ),
        
        -- Pre-compute entmod eligibility to avoid correlated EXISTS
        entmod_eligible AS (
            SELECT /*+ MATERIALIZE */
                em.emodsid AS tinsid,
                em.period,
                em.mft
            FROM entmod em
            WHERE em.status = 'O'
              AND em.mft IN (1, 9, 11, 12, 13, 14, 16, 64)
        ),
        
        -- Main query with optimized joins
        main_data AS (
            SELECT 
                tt.ROID,
                p.SEID,
                a.TIN,
                a.TINTT,
                a.TINFS,
                tt.TIMESID AS TINSID,
                SUBSTR(a.TP, 1, 35) AS TP,
                a.RISK AS C_RISK,
                tt.RISK AS H_RISK,
                GETSEGIND(tt.roid, tt.timesid) AS C_CASEIND,
                b.SEGIND AS H_CASEIND,
                NVL(tt.CONTCD, ' ') AS CONTACTCD,
                tt.EXTRDT,
                tt.RPTDT,
                a.casecode AS C_CASECODE,
                a.casecode AS CASECODE,
                tt.CODE AS H_CASECODE,
                a.subcode AS C_SUBCODE,
                a.subcode AS SUBCODE,
                tt.SUBCODE AS H_SUBCODE,
                ' ' AS TIMECODE,
                ' ' AS TIMEDESC,
                'T' AS TIMEDEF,
                a.GRADE AS C_GRADE,
                tt.GRADE AS H_GRADE,
                tt.HOURS,
                tt.BODCD,
                tour_sub.TOUR,
                tt.PRGNAME1,
                tt.PRGNAME2,
                b.TOTASSD,
                c.BAL_941_14,
                a.GRADE AS CASEGRADE,
                NVL(a.NAICSCD, ' ') AS NAICSCD,
                b.CCNIPSELECTCD,
                c.CNT_941_14,
                c.CNT_941,
                c.TDI_CNT_941,
                NVL(b.TDAcnt, 0) AS TDACNT,
                NVL(b.TDIcnt, 0) AS TDICNT,
                NVL((b.TDAcnt + b.TDIcnt), 0) AS MODCNT,
                DECODE(b.STATUS, 'O', STATIND(a.TINSID), 0) AS STATIND,
                NVL(b.ASSNFLD, TO_DATE('01/01/1900', 'mm/dd/yyyy')) AS ASSNFLD,
                CASE
                    WHEN b.segind IN ('A', 'C', 'I') THEN
                        GETASSNQUE(a.TIN, a.TINTT, a.TINFS, a.ASSNCFF, b.ASSNRO)
                    ELSE
                        b.ASSNRO
                END AS ASSNQUE,
                NVL(
                    DECODE(b.status,
                        'C', b.CLOSEDT,
                        'X', b.CLOSEDT,
                        TO_DATE('01/01/1900', 'mm/dd/yyyy')),
                    TO_DATE('01/01/1900', 'mm/dd/yyyy')
                ) AS CLOSEDT,
                NVL(b.DT_DOD, TO_DATE('01/01/1900', 'mm/dd/yyyy')) AS DT_DOD,
                b.XXDT,
                b.INITDT,
                b.DT_OA,
                b.DT_POA,
                TRUNC(ASSNPICKDT(a.TIN, a.TINFS, a.TINTT, b.STATUS, b.PROID, b.ROID)) AS PICKDT,
                b.DVICTCD,
                DECODE(DECODE(b.ZIPCDE, 00000,
                    ASSNQPICK(a.TIN, a.TINFS, a.TINTT, INTLQPICK(a.TIN, a.TINFS, a.TINTT, b.STATUS)),
                    DECODE(a.CITY, 'APO', ASSNQPICK(a.TIN, a.TINFS, a.TINTT, INTLQPICK(a.TIN, a.TINFS, a.TINTT, b.STATUS)),
                    'FPO',
                    ASSNQPICK(a.TIN, a.TINFS, a.TINTT, INTLQPICK(a.TIN, a.TINFS, a.TINTT, b.STATUS)),
                    ASSNQPICK(a.TIN, a.TINFS, a.TINTT, b.PROID))),
                    ',0', '0,0', '1,1', '2,2', '3,3', '4,4', '5,5', '6,6', '7,7', '?') AS QPICKIND,
                b.FLDHRS,
                NVL(b.EMPHRS, 0) AS EMPHRS,
                b.HRS,
                CASE
                    WHEN b.ORG = 'CP' THEN b.CCPHRS
                    ELSE GREATEST(NVL(b.TOTHRS, 0), NVL(b.EMPHRS, 0))
                END AS TOTHRS,
                c.IND_941 AS IND_941,
                CASE WHEN c.ind_941 = 0 THEN 'No' ELSE 'Yes' END AS FORMATTED_IND_941,
                b.HINFIND,
                DECODE(b.segind, 'A', a.AGEIND, 'C', a.AGEIND, 'I', a.AGEIND, 'C') AS AGEIND,
                TO_NUMBER(b.PDTIND) AS CAUIND,
                DECODE(
                    b.STATUS,
                    'O', 
                    CASE
                        WHEN DECODE(b.SEGIND, 'C', 1, 'A', 1, 'I', 1, 0) = 1
                             AND DECODE(a.casecode, '201', 1, '301', 1, '401', 1, '601', 1, 0) = 1
                             AND b.TOTASSD >= 10000
                             AND ee.tinsid IS NOT NULL
                             AND b.assnro + 150 < duedt(ee.period, ee.mft)
                        THEN 1
                        ELSE 0
                    END,
                    0
                ) AS PYRENT,
                DECODE(b.segind, 'A', a.PYRIND, 'C', a.PYRIND, 'I', a.PYRIND, 0) AS PYRIND,
                b.FATCAIND,
                b.FEDCONIND,
                b.FEDEMPIND,
                b.IRSEMPIND,
                b.L903,
                TO_NUMBER(NVL(a.LFIIND, 0)) AS LFIIND,
                b.LLCIND,
                DECODE(b.segind, 'A', a.RPTIND, 'C', a.RPTIND, 'I', a.RPTIND, 'F') AS RPTIND,
                b.THEFTIND,
                b.INSPCIND,
                b.OICACCYR,
                LPAD(NVL(a.RISK, 399) || NVL(a.ARISK, 'e'), 4, ' ') AS ARANK,
                0 AS TOT_IRP_INC,
                b.EMPTOUCH,
                a.LSTTOUCH,
                CASE
                    WHEN b.ORG = 'CP' THEN a.CCPTOUCH
                    ELSE GREATEST(a.TOTTOUCH, b.EMPTOUCH)
                END AS TOTTOUCH,
                NVL(a.STREET2, ' ') AS STREET2,
                b.PROID,
                0 AS TOT_INC_DELQ_YR,
                0 AS PRIOR_YR_RET_AGI_AMT,
                0 AS TXPER_TXPYR_AMT,
                0 AS PRIOR_ASSGMNT_NUM,
                a.AGI_AMT,
                TO_DATE('01/01/1900', 'mm/dd/yyyy') AS PRIOR_ASSGMNT_ACT_DT,
                c.BAL_941,
                selcode_sub.selcode AS SELCODE,
                REPLACE(b.STATUS, 'X', 'C') AS STATUS,
                NVL(b.SEGIND, '?') AS CASEIND,
                NVL(b.DISPCD, -1) AS DISPCD,
                b.CC,
                DECODE(a.ASSNCFF, '01/01/1900', b.ASSNRO, a.ASSNCFF) AS ASSNCFF,
                NVL(b.ASSNRO, TO_DATE('01/01/1900', 'mm/dd/yyyy')) AS ASSNRO,
                DECODE(b.segind, 'A', a.LDIND, 'C', a.LDIND, 'I', a.LDIND, 'F') AS LDIND,
                a.TPCTRL,
                NVL(a.RISK, 399) AS RISK,
                NVL(a.CITY, ' ') AS CITY,
                a.STATE,
                SUBSTR(NVL(a.TP2, ' '), 1, 35) AS TP2,
                SUBSTR(NVL(a.STREET, ' '), 1, 35) AS STREET,
                CASE
                    WHEN b.zipcde < 100000 THEN
                        TO_NUMBER(TO_CHAR(b.ZIPCDE, '09999'))
                    WHEN b.zipcde BETWEEN 99999 AND 999999999 THEN
                        NVL(TO_NUMBER(SUBSTR(TO_CHAR(b.ZIPCDE, '099999999'), -9, 5)), 0)
                    ELSE
                        NVL(TO_NUMBER(SUBSTR(TO_CHAR(b.ZIPCDE, '099999999999'), -12, 5)), 0)
                END AS ZIPCDE
        
            FROM ENT a
            INNER JOIN filtered_timetin tt 
                ON a.TINSID = tt.TIMESID
            INNER JOIN TRANTRAIL b 
                ON a.TINSID = b.TINSID
            CROSS APPLY TABLE(mft_ind_vals(b.tinsid, a.tinfs)) c
            LEFT JOIN entemp p 
                ON tt.roid = p.roid 
                AND p.eactive IN ('A', 'Y') 
                AND p.elevel >= 0
            LEFT JOIN (
                SELECT roid, tour
                FROM entemp
                WHERE elevel > 0
                  AND eactive IN ('A', 'Y')
            ) tour_sub 
                ON tt.roid = tour_sub.roid
            LEFT JOIN (
                SELECT emodsid, selcode
                FROM entmod
                WHERE rownum = 1
            ) selcode_sub 
                ON selcode_sub.emodsid = a.tinsid
            LEFT JOIN entmod_eligible ee 
                ON ee.tinsid = b.tinsid
        
            WHERE a.grade = ?
        )
        
        SELECT /*+ FIRST_ROWS(500) */
            ROID, SEID, TIN, TINTT, TINFS, TINSID, TP, C_RISK, H_RISK,
            C_CASEIND, H_CASEIND, CONTACTCD, EXTRDT, RPTDT, C_CASECODE,
            CASECODE, H_CASECODE, C_SUBCODE, SUBCODE, H_SUBCODE, TIMECODE,
            TIMEDESC, TIMEDEF, C_GRADE, H_GRADE, HOURS, BODCD, TOUR,
            PRGNAME1, PRGNAME2, TOTASSD, BAL_941_14, CASEGRADE, NAICSCD,
            CCNIPSELECTCD, CNT_941_14, CNT_941, TDI_CNT_941, TDACNT, TDICNT,
            MODCNT, STATIND, ASSNFLD, ASSNQUE, CLOSEDT, DT_DOD, XXDT,
            INITDT, DT_OA, DT_POA, PICKDT, DVICTCD, QPICKIND, FLDHRS,
            EMPHRS, HRS, TOTHRS, IND_941, FORMATTED_IND_941, HINFIND,
            AGEIND, CAUIND, PYRENT, PYRIND, FATCAIND, FEDCONIND, FEDEMPIND,
            IRSEMPIND, L903, LFIIND, LLCIND, RPTIND, THEFTIND, INSPCIND,
            OICACCYR, ARANK, TOT_IRP_INC, EMPTOUCH, LSTTOUCH, TOTTOUCH,
            STREET2, PROID, TOT_INC_DELQ_YR, PRIOR_YR_RET_AGI_AMT,
            TXPER_TXPYR_AMT, PRIOR_ASSGMNT_NUM, AGI_AMT, PRIOR_ASSGMNT_ACT_DT,
            BAL_941, SELCODE, STATUS, CASEIND, DISPCD, CC, ASSNCFF, ASSNRO,
            LDIND, TPCTRL, RISK, CITY, STATE, TP2, STREET, ZIPCDE
        FROM main_data
        """;
    
    /**
     * Simple connection test query.
     */
    public static final String CONNECTION_TEST = "SELECT 1 FROM DUAL";
    
    /**
     * Query to get database version info.
     */
    public static final String DATABASE_VERSION = 
            "SELECT banner FROM v$version WHERE ROWNUM = 1";
}
