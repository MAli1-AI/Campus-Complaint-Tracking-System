package com.campuscomplaint.service;

import com.campuscomplaint.model.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ComplaintService {
    private ComplaintService() {}

    // ===== Existing methods (unchanged but need to be present) =====

    public static void submitComplaint(Complaint complaint) {
        try {
            assignResolver(complaint);
            List<Complaint> all = FileHandler.readComplaints();
            all.add(complaint);
            FileHandler.writeComplaints(all);
            FileHandler.addNotification(complaint.getResolverId(), "New complaint assigned: " + complaint.getComplaintId());
            FileHandler.addNotification(complaint.getUserId(), "Your complaint submitted: " + complaint.getComplaintId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to submit complaint: " + e.getMessage(), e);
        }
    }

    private static void assignResolver(Complaint complaint) {
        Resolver best = getBestResolver(complaint.getCategory());
        if (best != null) {
            complaint.setResolverId(best.getUserId());
        } else {
            complaint.setResolverId("unassigned");
        }
    }

    private static Resolver getBestResolver(ComplaintCategory category) {
        try {
            List<User> users = FileHandler.readUsers();
            List<Resolver> resolvers = users.stream()
                .filter(u -> u instanceof Resolver)
                .map(u -> (Resolver) u)
                .filter(r -> !r.isFlagged())
                .filter(r -> r.canHandle(category))
                .collect(Collectors.toList());
            if (resolvers.isEmpty()) return null;
            Resolver best = resolvers.get(0);
            int minPending = countPendingComplaints(best.getUserId());
            for (Resolver r : resolvers) {
                int pending = countPendingComplaints(r.getUserId());
                if (pending < minPending) {
                    minPending = pending;
                    best = r;
                }
            }
            return best;
        } catch (Exception e) {
            throw new RuntimeException("Error finding best resolver: " + e.getMessage(), e);
        }
    }

    private static int countPendingComplaints(String resolverId) {
        List<Complaint> all = FileHandler.readComplaints();
        return (int) all.stream()
            .filter(c -> c.getResolverId().equals(resolverId))
            .filter(c -> c.getStatusPercent() < 100)
            .count();
    }

    public static void updateComplaintStatus(String complaintId, int newStatus, String resolverId) {
        try {
            List<Complaint> complaints = FileHandler.readComplaints();
            boolean found = false;
            for (Complaint c : complaints) {
                if (c.getComplaintId().equals(complaintId)) {
                    if (newStatus > c.getStatusPercent() && 
                        (newStatus == 25 || newStatus == 50 || newStatus == 75 || newStatus == 100)) {
                        c.setStatusPercent(newStatus);
                        if (newStatus == 100) {
                            c.setResolvedTimestamp(LocalDateTime.now());
                        }
                        FileHandler.writeComplaints(complaints);
                        FileHandler.addNotification(c.getUserId(), "Complaint " + complaintId + " status updated to " + newStatus + "%");
                        found = true;
                    } else {
                        throw new IllegalArgumentException("Invalid status transition from " + c.getStatusPercent() + " to " + newStatus);
                    }
                    break;
                }
            }
            if (!found) throw new IllegalArgumentException("Complaint not found: " + complaintId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update status: " + e.getMessage(), e);
        }
    }

    public static List<Complaint> getAllComplaints() {
        try {
            return FileHandler.readComplaints();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read complaints: " + e.getMessage(), e);
        }
    }

    public static List<Complaint> getComplaintsByUser(String userId) {
        return getAllComplaints().stream()
            .filter(c -> c.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    public static List<Complaint> getComplaintsByResolver(String resolverId) {
        return getAllComplaints().stream()
            .filter(c -> c.getResolverId().equals(resolverId))
            .collect(Collectors.toList());
    }

    public static void giveFeedback(String complaintId, int feedbackPercentage, String userId) {
        List<Complaint> complaints = FileHandler.readComplaints();
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(complaintId) && c.getUserId().equals(userId)) {
                if (c.getStatusPercent() == 100) {
                    c.setFeedback(feedbackPercentage);
                    FileHandler.writeComplaints(complaints);
                    FileHandler.addNotification(c.getResolverId(), "Feedback received for complaint " + complaintId);
                    return;
                } else {
                    throw new IllegalStateException("Complaint not resolved yet");
                }
            }
        }
        throw new IllegalArgumentException("Complaint not found or not owned by user");
    }

    public static double getAverageResolutionTimeForResolver(String resolverId) {
        List<Complaint> resolved = getComplaintsByResolver(resolverId).stream()
            .filter(c -> c.getStatusPercent() == 100)
            .collect(Collectors.toList());
        if (resolved.isEmpty()) return -1;
        long totalMinutes = 0;
        for (Complaint c : resolved) {
            long minutes = getResolutionTimeInMinutes(c);
            if (minutes >= 0) totalMinutes += minutes;
        }
        double avgHours = (double) totalMinutes / resolved.size() / 60.0;
        return Math.round(avgHours * 10.0) / 10.0;
    }

    private static long getResolutionTimeInMinutes(Complaint c) {
        if (c.getStatusPercent() == 100 && c.getResolvedTimestamp() != null) {
            return java.time.Duration.between(c.getTimestamp(), c.getResolvedTimestamp()).toMinutes();
        }
        return -1;
    }

    public static double getAverageFeedbackForResolver(String resolverId) {
        List<Complaint> withFeedback = getComplaintsByResolver(resolverId).stream()
            .filter(c -> c.getStatusPercent() == 100 && c.getFeedback() != -1)
            .collect(Collectors.toList());
        if (withFeedback.isEmpty()) return -1;
        double sum = withFeedback.stream().mapToInt(Complaint::getFeedback).sum();
        return sum / withFeedback.size();
    }

    public static Map<String, Double> getResolverPerformanceReport() {
        List<User> users = FileHandler.readUsers();
        Map<String, Double> report = new HashMap<>();
        for (User u : users) {
            if (u instanceof Resolver) {
                String id = u.getUserId();
                double avgTime = getAverageResolutionTimeForResolver(id);
                report.put(id, avgTime);
            }
        }
        return report;
    }

    // ===== New Methods =====

    public static void sendManagerQuery(String complaintId, String managerId, String query) {
        List<Complaint> complaints = FileHandler.readComplaints();
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(complaintId)) {
                c.setManagerQuery(query);
                FileHandler.writeComplaints(complaints);
                FileHandler.addNotification(c.getResolverId(), "Manager query on complaint " + complaintId);
                return;
            }
        }
        throw new IllegalArgumentException("Complaint not found: " + complaintId);
    }

    public static void sendResolverResponse(String complaintId, String resolverId, String response) {
        List<Complaint> complaints = FileHandler.readComplaints();
        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(complaintId) && c.getResolverId().equals(resolverId)) {
                c.setResolverResponse(response);
                FileHandler.writeComplaints(complaints);
                FileHandler.addNotification(getManagerId(), "Resolver responded to complaint " + complaintId);
                return;
            }
        }
        throw new IllegalArgumentException("Complaint not found or not assigned to this resolver");
    }

    private static String getManagerId() {
        List<User> users = FileHandler.readUsers();
        for (User u : users) {
            if (u instanceof Manager) return u.getUserId();
        }
        return "Manager";
    }

    public static void reassignComplaint(String complaintId, String newResolverId) {
        List<Complaint> complaints = FileHandler.readComplaints();
        List<User> users = FileHandler.readUsers();
        Resolver newResolver = null;
        for (User u : users) {
            if (u instanceof Resolver && u.getUserId().equals(newResolverId)) {
                newResolver = (Resolver) u;
                break;
            }
        }
        if (newResolver == null) throw new IllegalArgumentException("Resolver not found: " + newResolverId);

        for (Complaint c : complaints) {
            if (c.getComplaintId().equals(complaintId)) {
                if (c.getResolverId().equals(newResolverId)) {
                    // Already assigned to this resolver
                    return;
                }
                if (!newResolver.canHandle(c.getCategory())) {
                    throw new IllegalArgumentException("Resolver cannot handle this category");
                }
                String oldResolver = c.getResolverId();
                c.setResolverId(newResolverId);
                // Extend deadline by 48 hours to move out of overdue
                c.setDeadline(c.getDeadline().plusHours(48));
                c.setManagerQuery("Reassigned from " + oldResolver + " to " + newResolverId + " (deadline extended)");
                FileHandler.writeComplaints(complaints);
                FileHandler.addNotification(c.getUserId(), "Complaint " + complaintId + " reassigned to " + newResolverId);
                FileHandler.addNotification(newResolverId, "New complaint assigned: " + complaintId);
                if (oldResolver != null && !oldResolver.equals("unassigned")) {
                    FileHandler.addNotification(oldResolver, "Complaint " + complaintId + " removed from you");
                }
                return;
            }
        }
        throw new IllegalArgumentException("Complaint not found: " + complaintId);
    }

    public static void flagResolver(String resolverId) {
        List<User> users = FileHandler.readUsers();
        for (User u : users) {
            if (u instanceof Resolver && u.getUserId().equals(resolverId)) {
                ((Resolver) u).setFlagged(true);
                FileHandler.writeUsers(users);
                FileHandler.addNotification(resolverId, "You have been flagged by Manager");
                return;
            }
        }
        throw new IllegalArgumentException("Resolver not found: " + resolverId);
    }

    public static void unflagResolver(String resolverId) {
        List<User> users = FileHandler.readUsers();
        for (User u : users) {
            if (u instanceof Resolver && u.getUserId().equals(resolverId)) {
                ((Resolver) u).setFlagged(false);
                FileHandler.writeUsers(users);
                FileHandler.addNotification(resolverId, "You have been unflagged by Manager");
                return;
            }
        }
        throw new IllegalArgumentException("Resolver not found: " + resolverId);
    }

    public static List<Complaint> getOverdueComplaints() {
        List<Complaint> all = getAllComplaints();
        return all.stream()
                .filter(c -> c.isOverdue())
                .collect(Collectors.toList());
    }

    public static List<Complaint> getComplaintsWithManagerQuery() {
        List<Complaint> all = getAllComplaints();
        return all.stream()
                .filter(c -> c.getManagerQuery() != null && !c.getManagerQuery().isEmpty() && c.getResolverResponse().isEmpty())
                .collect(Collectors.toList());
    }

    public static Map<String, Long> getResolverCounts(String resolverId) {
        List<Complaint> assigned = getComplaintsByResolver(resolverId);
        Map<String, Long> counts = new HashMap<>();
        counts.put("total", (long) assigned.size());
        counts.put("resolved", assigned.stream().filter(c -> c.getStatusPercent() == 100).count());
        counts.put("pending", assigned.stream().filter(c -> c.getStatusPercent() < 100).count());
        return counts;
    }
}