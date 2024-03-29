package hideonbush3.springboot.happypet.service;

public interface MailService {
    public void sendMail(String email, String title, String body);
}