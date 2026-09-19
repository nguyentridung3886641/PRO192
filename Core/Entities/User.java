import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public abstract class User {
    private String userId;
    private String fullName;
    private String phone;
    private LocalDate dateOfBirth;
    private static final DateTimeFormatter Date_Format = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    User(String memberId,
         String fullName,
         String phone,
         LocalDate dateOfBirth) {
        this.userId = memberId;
        this.fullName = fullName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserId(){
        return userId;
    }
    public String getFullName(){
        return fullName;
    }
    public String getPhone(){
        return phone;
    }
    public LocalDate getDateOfBirth(){
        return dateOfBirth;
    }
    public void setUserId(String newMemberId){
        this.userId = newMemberId;
    }
    public void setFullName(String newFullName){
        this.fullName = newFullName;
    }
    public void setPhone(String newPhone){
        this.phone = newPhone;
    }
    public void setDateOfBirth(LocalDate newDateOfBirth){
        this.dateOfBirth = newDateOfBirth;
    }
}
