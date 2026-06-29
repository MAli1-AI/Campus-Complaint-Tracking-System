package com.campuscomplaint.model;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String USERS_FILE = "data/users.txt";
    private static final String COMPLAINTS_FILE = "data/complaints.txt";
    private static final String NOTIFICATIONS_FILE = "data/notifications.txt";

    private FileHandler() {}

    public static List<User> readUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        if (!file.exists()) return users;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;
                
                try {
                    String[] parts = line.split(",");
                    if (parts.length < 4) {
                        System.err.println("Skipping user line " + lineNumber + ": insufficient fields");
                        continue;
                    }

                    String id = parts[0].trim();
                    String name = parts[1].trim();
                    String pass = parts[2].trim();
                    String role = parts[3].trim();

                    if (id.isEmpty()) {
                        System.err.println("Skipping user line " + lineNumber + ": empty ID");
                        continue;
                    }

                    switch (role.toUpperCase()) {
                        case "STUDENT":
                            if (parts.length >= 6) {
                                users.add(new Student(id, name, pass, parts[4].trim(), Integer.parseInt(parts[5].trim())));
                            } else {
                                System.err.println("Skipping student line " + lineNumber + ": missing department/semester");
                            }
                            break;
                        case "PROFESSOR":
                            if (parts.length >= 6) {
                                users.add(new Professor(id, name, pass, parts[4].trim(), parts[5].trim()));
                            } else {
                                System.err.println("Skipping professor line " + lineNumber + ": missing department/designation");
                            }
                            break;
                        case "STAFF":
                            if (parts.length >= 5) {
                                users.add(new StaffMember(id, name, pass, parts[4].trim()));
                            } else {
                                System.err.println("Skipping staff line " + lineNumber + ": missing office location");
                            }
                            break;
                        case "MANAGER":
                            users.add(new Manager(id, name, pass));
                            break;
                        case "RESOLVER":
                            ComplaintCategory specialCat = null;
                            if (parts.length >= 5 && !parts[4].trim().equalsIgnoreCase("General") && !parts[4].trim().isEmpty()) {
                                specialCat = instantiateCategoryFromString(parts[4].trim());
                            }
                            boolean flagged = parts.length >= 6 && parts[5].trim().equalsIgnoreCase("true");
                            Resolver resolver = new Resolver(id, name, pass, specialCat);
                            resolver.setFlagged(flagged);
                            users.add(resolver);
                            break;
                        default:
                            System.err.println("Skipping user line " + lineNumber + ": unknown role '" + role + "'");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Skipping user line " + lineNumber + ": invalid number format");
                } catch (Exception e) {
                    System.err.println("Skipping user line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users: " + e.getMessage());
        }
        return users;
    }

    public static void writeUsers(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (User u : users) {
                if (u != null) {
                    pw.println(u.fileFormat());
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing users: " + e.getMessage());
        }
    }

    public static void addNotification(String userId, String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(NOTIFICATIONS_FILE, true))) {
            if (userId == null) userId = "unknown";
            if (message == null) message = "";
            pw.println(userId + "," + message.replace(",", ";") + "," + LocalDateTime.now());
        } catch (IOException e) {
            System.err.println("Error writing notification: " + e.getMessage());
        }
    }

    public static List<String> getNotifications(String userId) {
        List<String> userNotifications = new ArrayList<>();
        File file = new File(NOTIFICATIONS_FILE);
        if (!file.exists()) return userNotifications;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] parts = line.split(",", 3);
                    if (parts.length >= 3 && parts[0].equals(userId)) {
                        userNotifications.add(parts[1] + " [" + parts[2] + "]");
                    }
                } catch (Exception e) {
                    System.err.println("Skipping malformed notification line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading notifications: " + e.getMessage());
        }
        return userNotifications;
    }

    public static List<Complaint> readComplaints() {
        List<Complaint> complaints = new ArrayList<>();
        File file = new File(COMPLAINTS_FILE);
        if (!file.exists()) return complaints;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split("\\|");
                    if (parts.length < 13) {
                        System.err.println("Skipping complaint line " + lineNumber + ": insufficient fields (found " + parts.length + ")");
                        continue;
                    }

                    String complaintId = parts[0].trim();
                    String userId = parts[1].trim();
                    ComplaintCategory category = instantiateCategoryFromString(parts[2].trim());
                    int urgency = Integer.parseInt(parts[3].trim());
                    int statusPercent = Integer.parseInt(parts[4].trim());
                    String resolverId = parts[5].trim();
                    String title = unescapePipe(parts[6].trim());
                    String desc = unescapePipe(parts[7].trim());
                    int feedback = Integer.parseInt(parts[8].trim());
                    LocalDateTime timestamp = LocalDateTime.parse(parts[9].trim());
                    LocalDateTime deadline = LocalDateTime.parse(parts[10].trim());
                    String imgPath = parts[11].trim().equalsIgnoreCase("null") ? null : parts[11].trim();
                    LocalDateTime resolvedTimestamp = null;
                    if (parts.length > 12 && !parts[12].trim().equals("null")) {
                        resolvedTimestamp = LocalDateTime.parse(parts[12].trim());
                    }
                    String managerQuery = (parts.length > 13) ? unescapePipe(parts[13].trim()) : "";
                    String resolverResponse = (parts.length > 14) ? unescapePipe(parts[14].trim()) : "";

                    Complaint complaint = new Complaint(complaintId, userId, category, title, desc, urgency);
                    complaint.setStatusPercent(statusPercent);
                    complaint.setResolverId(resolverId);
                    if (statusPercent == 100 && feedback >= 0 && feedback <= 100) {
                        try {
                            complaint.setFeedback(feedback);
                        } catch (IllegalStateException e) { /* ignore */ }
                    }
                    complaint.setTimestamp(timestamp);
                    complaint.setDeadline(deadline);
                    complaint.setResolvedTimestamp(resolvedTimestamp);
                    if (imgPath != null) complaint.setImagePath(imgPath);
                    complaint.setManagerQuery(managerQuery);
                    complaint.setResolverResponse(resolverResponse);
                    complaints.add(complaint);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping complaint line " + lineNumber + ": invalid number format");
                } catch (DateTimeParseException e) {
                    System.err.println("Skipping complaint line " + lineNumber + ": invalid date/time format");
                } catch (Exception e) {
                    System.err.println("Skipping complaint line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading complaints: " + e.getMessage());
        }
        return complaints;
    }

    public static void writeComplaints(List<Complaint> complaints) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(COMPLAINTS_FILE))) {
            for (Complaint c : complaints) {
                if (c == null) continue;
                String img = (c.getImagePath() == null) ? "null" : c.getImagePath();
                String resolved = (c.getResolvedTimestamp() == null) ? "null" : c.getResolvedTimestamp().toString();
                String title = escapePipe(c.getTitle());
                String desc = escapePipe(c.getDescription());
                String mq = escapePipe(c.getManagerQuery());
                String rr = escapePipe(c.getResolverResponse());
                pw.println(c.getComplaintId() + "|" + c.getUserId() + "|" + c.getCategory().getCategoryType() + "|" +
                           c.getUrgencyLevel() + "|" + c.getStatusPercent() + "|" + c.getResolverId() + "|" +
                           title + "|" + desc + "|" + c.getFeedback() + "|" +
                           c.getTimestamp() + "|" + c.getDeadline() + "|" + img + "|" + resolved + "|" +
                           mq + "|" + rr);
            }
        } catch (IOException e) {
            System.err.println("Error writing complaints: " + e.getMessage());
        }
    }

    private static ComplaintCategory instantiateCategoryFromString(String type) {
        if (type == null) return new AcademicCategory();
        switch (type.toUpperCase()) {
            case "ACADEMIC": return new AcademicCategory();
            case "ADMINISTRATIVE": return new AdministrativeCategory();
            case "IT": return new ITCategory();
            case "INFRASTRUCTURE": return new InfrastructureCategory();
            case "LIBRARY": return new LibraryCategory();
            default: return new AcademicCategory();
        }
    }

    private static String escapePipe(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|");
    }

    private static String unescapePipe(String s) {
        if (s == null) return "";
        return s.replace("\\|", "|");
    }

    public static String generateComplaintId(String roleCode, ComplaintCategory category, List<Complaint> existing) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        String catCode = category.getCategoryType().toUpperCase().substring(0, Math.min(4, category.getCategoryType().length()));
        while (catCode.length() < 4) catCode += "X";
        String prefix = roleCode + "-" + catCode + "-";
        int maxSeq = 0;
        if (existing != null) {
            for (Complaint c : existing) {
                if (c != null && c.getComplaintId().startsWith(prefix)) {
                    try {
                        String seqPart = c.getComplaintId().substring(prefix.length());
                        int seq = Integer.parseInt(seqPart);
                        if (seq > maxSeq) maxSeq = seq;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return prefix + String.format("%04d", maxSeq + 1);
    }
}
