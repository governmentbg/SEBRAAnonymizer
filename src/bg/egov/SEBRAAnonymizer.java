package bg.egov;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.ibm.icu.text.Transliterator;

/**
 * Anonymizes SEBRA datasets
 */
public class SEBRAAnonymizer {

    private static final List<String> NAME_SUFFIXES = Arrays.asList("ова", "ева", "ска", "ов", "ев", "ски", "ин", 
            "ина", "ич", "ян", "ан", "чки", "чка", "цки", "цка", "шки", "шка", "зки", "зка", "ян", "иан");

    private static final List<String> OTHER_PERSONAL_NAMES = Arrays.asList("ахмед", "ахмет", "мехмед", "сезгин", "мюмюн", "мустафа", "халил", "гюнер", "гюнай", 
            "сунай", "байрам", "ибрям", "хасан", "джейхан", "ерджан", "рамадан", "фатме", "редже", "хамид", "хамди", "юсеин", "хусейн", "сабри", " али", 
            "адам", "анави", "аврам", "арон", "финци", "шварц", " леви", "ицхак", "исак", "коен", "соломон", "яков", "щайн", "берг", 
            "джераси", "езра", "фридман", "йосиф", "барух", "давид", "бехар", "илиева", "николова", "петрова", "иванова");
    
    private static final Transliterator TRANSLITERATOR = Transliterator.getInstance("Latin-Cyrillic");
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("d/M/yyyy");
    
    private static final MessageDigest DIGEST;
    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private static final List<String> NON_PERSONAL_INDICATORS = Arrays.asList("мбал", "община", "ет ", " ет", "медицински", 
            "оод", "сд ", "камара", "аптека", "агенция", " ад", "еад", "еод", "дззд", "банка", "зк ", " съд", "университет", 
            "асоциация", "фондация", "зпк", " сие", "холдинг", "българск", "европейск", "руски", "руска", "турск", "фонд", 
            "сръбск", "македонск", "студентск", "румънск", "германск", "италианск", "арменск", "католическ", "протестантск", 
            "мюсюлманск", "английск", "британск", "испанск", "френск", "норвежк", "бритиш", " клуб", "съвет", "общност", 
            "вту ", "тп ", "тб ", "дсп ", "тпг ", "банк", " и ", "стз", "къща", "театър", "кооперация", "институт", "музей", "дружество",
            "нуфи", "соу ", "оу ", "пг ", "пгее", "ипсмп", "апимп", "апмп", "аиппдм", " груп", "пги ", "дирекция", "верига", "стопанска",
            "гимназия", "гпче", "гпие", "станция", "читалище", "пгехт", "пго", "пгтс", "пгим", "пгто", "пгпт", "пгс", "пгпо", "нгдек", "нгпи", 
            "пгтхт", "пгдва", "спге", "сгсаг","пгметт", "пгххт", "пгхтд", "пгмт", "цнпх", "пгте", "пглв", "пгох", "ипазр", "патг", "пмг", 
            "обединение", "ипжз", "филиал", " клон", "тпки", "комплекс", "продукт", "палата", "министерство", "ауто", " база", "секюрити", 
            "библиотека", "област", "народна ", "сдружение", "общин", "сбдпл", "аиппмп", "лаборатор", "градина", "училище", "ппок", "амцсп", 
            "филм ", "център", "ввму", "биогаз", "амбулатор", "агро ", "универс", "мбпл", "обединен", " комерс", "артс ", "компания", 
            "прокуратура", "чси ", " унив", "комисия", "адвокат", "адв.", "хотел", " хоте", "оусв", "манастир", "консорциум", "ентърпр",
            "импорт", "експорт", "импекс", " стил ", "селскостоп", "инженер", "мост ", "депозитар", "нотариус", "пето ", "изба ", 
            "сбал", "мбал",  "аппмп", "асмп", "дмсгд", " дент", "енерджи", "airline", "експрес", "ресторант");
    
    private static final Pattern WHITESPACE = Pattern.compile("\\s+");
    
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage arguments: <path to source CSV> <path to destination CSV> <path to names mapping CSV>");
        }
        Path saltFile = Path.of("salt.txt");
        if (!Files.exists(saltFile)) {
            Files.createFile(saltFile);
        }
        String salt = Files.readString(saltFile);
        if (StringUtils.isBlank(salt)) {
            salt = RandomStringUtils.randomAlphanumeric(300);
            Files.writeString(saltFile, salt);
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
                org.setFrom(LocalDate.parse(record.get(3).trim().split("\\s+")[0], DATE_FORMAT).atStartOfDay().minusDays(1));
                if (record.get(4).endsWith("3333")) {
                    org.setTo(LocalDateTime.now().plusWeeks(1));
                } else {
                    org.setTo(LocalDate.parse(record.get(4), DATE_FORMAT).atStartOfDay().plusDays(1));
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
                        
                        if (untranslitaratedBeneficiary.matches(".+\\b\\d{10}\\b") || untranslitaratedBeneficiary.toLowerCase().contains("ЕГН")) {
                            // edge case where the EGN is part of the beneficiary field
                            untranslitaratedBeneficiary = untranslitaratedBeneficiary.replaceAll("\\d+", "").replace("ЕГН", "");
                            row.set(1, untranslitaratedBeneficiary);
                        }
                        
                        String beneficiary = untranslitaratedBeneficiary.toLowerCase();
                        if (isPersonalName(untranslitaratedBeneficiary) 
                                && !untranslitaratedBeneficiary.contains("\"") 
                                && !untranslitaratedBeneficiary.contains(".")
                                && !untranslitaratedBeneficiary.contains("'")
                                && !untranslitaratedBeneficiary.trim().startsWith("ЕТ")
                                && NON_PERSONAL_INDICATORS.stream().noneMatch(s -> beneficiary.contains(s))) {
                            // In order to include parts of the person's name, there's a need to amend the decision of the council of ministers
                            //String anonymizedName = untranslitaratedBeneficiary.substring(0, untranslitaratedBeneficiary.indexOf(' '));
                            //row.set(1, anonymizedName + " ***");
                            row.set(1, "Физическо лице");
                        }
                        row.set(7, reason1.trim());
                        row.set(8, reason2.trim());
                        
                        if (header) {
                            row.add("ORGANIZATION");
                            row.add("PRIMARY_ORGANIZATION");
                            row.add("PRIMARY_ORG_CODE");
                            row.add("CLIENT_NAME_HASH");
                        } else {
                            List<Organization> orgs = organizations.get(record.get(4));
                            if (orgs != null) {
                                if (orgs.size() == 1) {
                                    Organization org = orgs.get(0);
                                    row.add(org.getName());
                                } else {
                                    boolean foundOrganization = false;
                                    for (Organization org : orgs) {
                                        LocalDateTime txDate = LocalDate.parse(record.get(0), DATE_FORMAT).atStartOfDay();
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
                            String primaryOrganizationCode = record.get(4).substring(0, 3);
                            List<Organization> primaryOrgs = organizations.get(primaryOrganizationCode + StringUtils.repeat("*", 7));
                            if (primaryOrgs != null) {
                                boolean foundOrganization = false;
                                for (Organization org : primaryOrgs) {
                                    LocalDateTime txDate = LocalDate.parse(record.get(0), DATE_FORMAT).atStartOfDay();
                                    if (txDate.isAfter(org.getFrom()) && txDate.isBefore(org.getTo())) {
                                        row.add(org.getName());
                                        foundOrganization = true;
                                        break;
                                    }
                                }
                                if (!foundOrganization) {
                                    row.add(primaryOrgs.get(primaryOrgs.size() - 1).getName());
                                }
                            } else {
                                row.add("");
                            }
                            row.add(primaryOrganizationCode);
                            
                            // hash a salt + normalized, untransliterated, lowercase beneficary name
                            row.add(bytesToHex(DIGEST.digest((salt + beneficiary.replaceAll("\\s+", " ")).getBytes(StandardCharsets.UTF_8))));
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
        String lowerCaseString = str.toLowerCase().trim();
        long wordCount = WHITESPACE.matcher(lowerCaseString).results().count();
        // 4 word names are the exception, so we have to filter out beneficiaries with short words in them
        if (wordCount == 3) {
            String[] parts = lowerCaseString.split("\\s+");
            for (String part : parts) {
                if (part.length() <= 3) {
                    return false;
                }
            }
        }
        
        return (wordCount == 2 || wordCount == 3) && 
                (NAME_SUFFIXES.stream().anyMatch(s -> lowerCaseString.matches(".+\\p{L}{2,}" + s + "\\b.+") || 
                        OTHER_PERSONAL_NAMES.stream().anyMatch(name -> lowerCaseString.contains(name))));
    }

    private static String untransliterate(String reason) {
        String result = TRANSLITERATOR.transliterate(reason);
        return result;
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
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
