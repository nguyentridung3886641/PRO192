import java.time.LocalDate;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class MemberManager {

    private final IMemberDAO memberDAO;
    private final Scanner scanner;

    MemberManager(IMemberDAO memberDAO, Scanner scanner){
        this.memberDAO = memberDAO;
        this.scanner = scanner;
    }

    public void menu(){

        System.out.println("========== MEMBER MANAGEMENT ==========");
        System.out.println("1. Add Member");
        System.out.println("2. Display Member");
        System.out.println("3. Search Member");
        System.out.println("4. Update Member");
        System.out.println("5. Delete Member");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");

        try{
            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> addMember();
                case 2 -> displayMember();
                case 3 -> searchMember();
                case 4 -> updateMember();
                case 5 -> deleteMember();
                case 6 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice! Please enter 1-6.");
            }
        }
        catch (InputMismatchException e){
            System.out.println("Invalid input! Please enter a number.");
        }
    }
    private void addMember(){
        String userId;
        String fullName;
        String phone;
        LocalDate dateOfBirth;
        LocalDate registrationDate;

        while(true){
            System.out.println("Enter user ID: ");
            userId = scanner.nextLine();

            if (memberDAO.findById(userId) != null) {
                System.out.println("ID already existed! Try another one");
            }
            else{
                break;
            }
        }

        System.out.println("Enter full name: ");
        fullName = scanner.nextLine().trim();

        System.out.println("Enter phone number: ");
        phone = scanner.nextLine().trim();

        System.out.println("Enter dateOfBirth: ");
        String strDateOfBirth = scanner.nextLine().trim();
        dateOfBirth = LocalDate.parse(strDateOfBirth, User.DATE_FORMAT);

        registrationDate = LocalDate.now();

        Member member = new Member(userId, fullName, phone, dateOfBirth, registrationDate);

        if(memberDAO.add(member)){
            System.out.println("Member added successfully");
        }
        else{
            System.out.println("Failed to add member");
        }
    }

    private void displayMember(){

        List<Member> members = memberDAO.getAll();

        if(members.isEmpty()){
            System.out.println("No members found");
        }

        for(Member member : members){
            System.out.println(member);
        }
    }

    private void searchMember(){
        int choice;

        do{
            System.out.println("\n===== SEARCH MEMBER =====");
            System.out.println("1. Search by ID");
            System.out.println("2. Search by Name");
            System.out.println("3. Back");
            System.out.println("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice){
                case 1 -> searchMemberById();
                case 2 -> searchMemberByName();
                case 3 -> System.out.println("Back to main menu");
                default -> System.out.println("Invalid choice");
            }

        }while(choice != 3);
    }

    private void searchMemberById(){
        System.out.println("Enter user ID: ");
        String userId = scanner.nextLine();

        Member member = memberDAO.findById(userId);

        if(member == null){
            System.out.println("Member not found");
        }
        else{
            System.out.println(member);
        }

    }

    private void searchMemberByName(){
        System.out.println("Enter keyword: ");
        String keyword = scanner.nextLine();

        List<Member> result = memberDAO.searchByName(keyword);

        if(result.isEmpty()){
            System.out.println("No member found.");
            return;
        }

        for(Member member : result){
            System.out.println(member);
        }
    }

    private void updateMember(){
        System.out.println("Enter member's ID to update: ");
        String userId = scanner.nextLine();

        Member member = memberDAO.findById(userId);

        if(member == null){
            System.out.println("Member not found");
            return;
        }

        System.out.println(member);
        System.out.println("==========NEW INFO==========");
        System.out.print("New Name: ");
        String name = scanner.nextLine();
        System.out.print("New Phone number: ");
        String phone = scanner.nextLine();
        System.out.print("New date of Birth: ");
        String strDOB = scanner.nextLine();
        LocalDate DOB = LocalDate.parse(strDOB, User.DATE_FORMAT);

        if(memberDAO.update(member, name, phone, DOB)){
            System.out.println("Member updated successfully");
        }
        else{
            System.out.println("Update failed");
        }
    }

    private void deleteMember(){
        System.out.println("Enter userId: ");
        String userId = scanner.nextLine();

        Member member = memberDAO.findById(userId);

        if(member == null){
            System.out.println("Member not found");
            return;
        }

        System.out.println("Member info: ");
        System.out.println(member);
        System.out.println("Are you sure want to delete this member (Y/N)? ");
        String choice = scanner.nextLine();

        if (choice.trim().equalsIgnoreCase("Y")) {
            if (memberDAO.delete(member)) {
                System.out.println("Member deleted successfully.");
            }
            else {
                System.out.println("Delete failed.");
            }
        }
        else if (choice.trim().equalsIgnoreCase("N")) {
            System.out.println("Delete cancelled.");
        } else {
            System.out.println("Invalid input.");
        }
    }

}
