package bg.egov;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.text.Transliterator;

/**
 * Anonymizes SEBRA datasets
 */
public class SEBRAAnonymizer {

    private static final List<String> NAME_SUFFIXES = Arrays.asList("ова", "ева", "ска", "ов", "ев", "ски", "ин", 
            "ина", "ич", "ян", "ан", "чки", "чка", "цки", "цка", "шки", "шка", "зки", "зка");

    private static final Transliterator TRANSLITERATOR = Transliterator.getInstance("Latin-Cyrillic");
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    
    private static final List<String> NON_PERSONAL_INDICATORS = Arrays.asList("мбал", "община", "ет ", "медицински", 
            "оод", "сд ", "камара", "аптека", "агенция", " ад", "еад", "еод", "банка", "зк ", " съд", "университет", 
            "асоциация", "зпк", " сие"); 
    
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage arguments: <path to source CSV> <path to destination CSV> <path to names mapping CSV>");
        }
        Map<String, List<Organization>> organizations = new HashMap<>();
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(args[2])), "cp1251")) {
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            for (CSVRecord record : parser) {
                List<Organization> list = organizations.computeIfAbsent(record.get(0), (k) -> new ArrayList<>());
                Organization org = new Organization();
                org.setCode(record.get(0));
                String name = record.get(1);
                String description = record.get(2);
                if (description.length() > name.length()) {
                    org.setName(description);
                } else {
                    org.setName(name);
                }
                org.setFrom(LocalDate.parse(record.get(3).trim().split(" ")[0], DATE_FORMAT).atStartOfDay());
                if (record.get(4).endsWith("3333")) {
                    org.setTo(LocalDateTime.now().plusWeeks(1));
                } else {
                    org.setTo(LocalDate.parse(record.get(4), DATE_FORMAT).atStartOfDay());
                }
                list.add(org);
            }
        }
        try (Reader reader = new InputStreamReader(new BufferedInputStream(new FileInputStream(args[0])), "cp1251"); 
                OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(args[1])), 
                        StandardCharsets.UTF_8)) {
            CSVParser parser = CSVParser.parse(reader, CSVFormat.DEFAULT);
            try (CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT)) {
                int anonymizedRecords = 0;
                int classified = 0;
                boolean header = true;
                for (CSVRecord record : parser) {
                    try {
                        String reason1 = record.get(7);
                        String reason2 = record.get(8);
                        reason1 = anonymize(reason1.toLowerCase());
                        reason2 = anonymize(reason2.toLowerCase());
                     
                        if (!reason1.equalsIgnoreCase(record.get(7)) || !reason2.equalsIgnoreCase(record.get(8))) {
                            anonymizedRecords++;
                        }
                        if (isClassifiedInformation(reason1) || isClassifiedInformation(reason2)) {
                            classified++;
                            continue;
                        }
                                               
                        List<String> row = new ArrayList<>();
                        for (int i = 0; i < record.size(); i ++) {
                            row.add(record.get(i).trim());
                        }
                        
                        String untranslitaratedBeneficiary = untransliterate(record.get(1));
                        if (isPersonalName(untranslitaratedBeneficiary) 
                                && !untranslitaratedBeneficiary.contains("\"") 
                                && !untranslitaratedBeneficiary.contains(".")
                                && !untranslitaratedBeneficiary.contains("'")
                                && !untranslitaratedBeneficiary.trim().startsWith("ЕТ")
                                && NON_PERSONAL_INDICATORS.stream().noneMatch(s -> untranslitaratedBeneficiary.toLowerCase().contains(s))) {
                            String anonymizedName = untranslitaratedBeneficiary.substring(0, untranslitaratedBeneficiary.indexOf(' '));
                            row.set(1, anonymizedName + " ***");
                        } else if (untranslitaratedBeneficiary.matches(".+\\b\\d{10}\\b") || untranslitaratedBeneficiary.contains("ЕГН")) {
                            // edge case where the EGN is part of the beneficiary field
                            row.set(1, untranslitaratedBeneficiary.replaceAll("\\d+", ""));
                        }
                        row.set(7, reason1.trim());
                        row.set(8, reason2.trim());
                        
                        if (header) {
                            row.add("ORGANIZATION");
                            row.add("PRIMARY_ORGANIZATION");
                        } else {
                            List<Organization> orgs = organizations.get(record.get(4));
                            if (orgs != null) {
                                if (orgs.size() == 1) {
                                    Organization org = orgs.get(0);
                                    row.add(org.getName());
                                } else {
                                    boolean foundOrganization = false;
                                    for (Organization org : orgs) {
                                        // +1 to avoid missing transactions on the day of change
                                        LocalDateTime txDate = LocalDate.parse(record.get(0), DATE_FORMAT).atStartOfDay().plusDays(1);
                                        if (txDate.isAfter(org.getFrom()) && txDate.isBefore(org.getTo())) {
                                            row.add(org.getName());
                                            foundOrganization = true;
                                            break;
                                        }
                                    }
                                    if (!foundOrganization) {
                                        row.add(orgs.get(orgs.size() - 1).getName());
                                    }
                                }
                            }
                            // add primary organization
                            List<Organization> primaryOrgs = organizations.get(record.get(4).substring(0, 3) + StringUtils.repeat("*", 7));
                            if (primaryOrgs != null) {
                                row.add(primaryOrgs.get(primaryOrgs.size() - 1).getName());
                            } else {
                                row.add("");
                            }
                        }
                        printer.printRecord(row);
                        header = false;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                System.out.println("Anomymized records " + anonymizedRecords);
                System.out.println("Classified records " + classified);
            }
        }
    }

    private static boolean isClassifiedInformation(String reason) {
        reason = reason.toLowerCase();
        if ((reason.contains("класифиц") && !reason.contains("некласифиц")) 
                || reason.contains("секретн") || reason.contains("секрете") || reason.contains("поверит") || reason.contains("ззки")) {
            return true;
        }
        return false;
    }

    private static String anonymize(String reason) {
        
        // if the field contains three names, skip it
        // some reasons are transliterated, so we need to untransliterate them
        String untranslitaratedReason = untransliterate(reason).toLowerCase();
        if (isPersonalName(untranslitaratedReason)) {
            return "";
        }
        // remove any address
        if (untranslitaratedReason.matches(".+\\bбул\\..+") || untranslitaratedReason.matches(".+\\bул\\..+") || untranslitaratedReason.matches(".+\\bбл\\..+")) {
            return "";
        }
        
        //replacing EGN/LNCH, partial ones or incorrect ones with extra digit
        return replaceEGN(reason);
    }

    public static String replaceEGN(String reason) {
        return reason.replaceAll("\\b\\d{9,11}\\b", "").replaceAll("ЕГН\\d{9,11}\\b", "").toUpperCase();
    }
    
    private static boolean isPersonalName(String str) {
        return StringUtils.countMatches(str, ' ') == 2 && 
                NAME_SUFFIXES.stream().anyMatch(s -> str.toLowerCase().matches(".+\\p{L}{2,}" + s + "\\b.+"));
    }

    private static String untransliterate(String reason) {
        String result = TRANSLITERATOR.transliterate(reason);
        return result;
    }

    public static class Organization {
        private String code;
        private LocalDateTime from;
        private LocalDateTime to;
        private String name;
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public LocalDateTime getFrom() {
            return from;
        }
        public void setFrom(LocalDateTime from) {
            this.from = from;
        }
        public LocalDateTime getTo() {
            return to;
        }
        public void setTo(LocalDateTime to) {
            this.to = to;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }
}
