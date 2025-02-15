package doancuoikhoa.login.service;

import doancuoikhoa.login.data.Data;
import doancuoikhoa.login.entities.*;
import doancuoikhoa.login.enums.ContractStatus;
import doancuoikhoa.login.enums.RentalRequestStatus;
import doancuoikhoa.login.enums.RoomStatus;
import doancuoikhoa.login.utils.Utils;
import doancuoikhoa.login.view.Menu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TenantService {

    RoomService roomService = new RoomService();
    ContractService contractService = new ContractService();
    RentalRequestService rentalRequestService = new RentalRequestService();

    // Tìm phòng theo khoảng giá
    public void searchRoomsByPrice(Scanner scanner, User user) {
        List<Room> roomss = new ArrayList<>();
        System.out.println("Mời bạn nhập khoảng giá thấp nhất");
        BigDecimal minPrice = Utils.inputBigDecimal(scanner);
        boolean existedRooms = false;
        System.out.println("Mời bạn nhập khoảng giá cao nhất");
        BigDecimal maxPrice = Utils.inputBigDecimal(scanner);
        for (Room room : Data.rooms) {
            if (room.getPrice().compareTo(minPrice) >= 0 && room.getPrice().compareTo(maxPrice) <= 0 /*&& room.getRoomStatus() == RoomStatus.AVAIABL*/) {
                roomss.add(room);
                existedRooms = true;

            }

        }
        // Nếu tồn tại phòng thì hiển thị, không thì thông báo lỗi
        if(existedRooms){
            displayRoomforTenant(roomss);
            askTenantifWantToAddRRoomToFavourList(scanner,user);
        }
        else {
            System.out.println("Không tìm thấy phòng phù hợp");

        }
    }

    public void searchRoomsByLocation(Scanner scanner, User user){
        List<Room> roomss = new ArrayList<>();
        System.out.println("Mời bạn nhập vào địa chỉ phòng cần tìm");
        String location = scanner.nextLine();
        // Loại bỏ dấu của chuỗi nhập vào
        String searchResult = Utils.removeAccents(location).toLowerCase();
        boolean existedRooms = false;
        for (Room room : Data.rooms) {
            String normalAddress = Utils.removeAccents(room.getLocation()).toLowerCase();
            if (normalAddress.contains(searchResult) && room.getRoomStatus() == RoomStatus.AVAIABLE){
                roomss.add(room);
                existedRooms = true;

            }

        }
        if(existedRooms){
            displayRoomforTenant(roomss);
            askTenantifWantToAddRRoomToFavourList(scanner,user);
        }else{
            System.out.println("Không tìm thấy phòng phù hợp.");
        }

    }
    public void displayRoomforTenant(List<Room> rooms){
        System.out.println("=============================== Danh sách phòng trọ ==============================================================");
        System.out.printf("%-10s %-20s %-30s %-15s %-15s %-10s \n",
                "ID", "Mô tả", "Vị trí", "Loại phòng", "Giá", "Trạng thái");
        System.out.println("==================================================================================================================");

        for (Room room: rooms) {
            String roomStatus = (room.getRoomStatus() != null) ? room.getRoomStatus().toString() : "N/A";

            // Giới hạn mô tả tối đa 20 ký tự
            String description = room.getDescription();
            if (description.length() > 20) {
                description = description.substring(0, 17) + "...";
            }

            System.out.printf("%-10s %-20s %-30s %-15s %-15s %-10s\n",
                    room.getId(),
                    room.getDescription(),
                    room.getLocation(),
                    room.getPropertyType(),
                    room.getPrice().toString(),
                    roomStatus
            );
        }
        System.out.println("==================================================================================================================");

    }
    public void searchRoomsByType(Scanner scanner, User user){
        List<Room> roomss = new ArrayList<>();
        boolean existedRooms = false;
        System.out.println("Mời bạn nhập vào loại phòng cần tìm");
        String type = scanner.nextLine();
        String typeAfterRemove = Utils.removeAccents(type).toLowerCase();
        for (Room room: Data.rooms) {
            String typeRoomAfter = Utils.removeAccents(room.getPropertyType()).toLowerCase();
            if(typeRoomAfter.contains(typeAfterRemove) && room.getRoomStatus() == RoomStatus.AVAIABLE){
                roomss.add(room);
                existedRooms = true;
            }
        }
        if(existedRooms){
            displayRoomforTenant(roomss);
            askTenantifWantToAddRRoomToFavourList(scanner,user);

        }else{
            System.out.println("Không tìm thấy loại phòng phù hợp.");
        }
    }
    public void askTenantifWantToAddRRoomToFavourList(Scanner scanner, User user) {
        Menu menu =Menu.getInstance();
        String choice;
        System.out.println("Bạn có muốn thêm phòng trọ nào vào danh sách yêu thích không(Y/N)?");
        choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("Y")) {
            do {

                addRoomToFavouriteList(scanner, user.getId());
                System.out.println("Bạn có muốn tiếp tục thêm không(Y/N)");
                choice = scanner.nextLine();
            } while (choice.equalsIgnoreCase("Y"));
        }else {
            menu.selectMenuSearchRoom(scanner,user);
        }
    }
    public void addRoomToFavouriteList(Scanner scanner, int tenantId){
        System.out.println("Mời bạn nhập vào id căn phòng yêu thích");
        boolean existedRooms = false;
        String id = scanner.nextLine();
        for (Room room: Data.rooms) {
            if(room.getId().equalsIgnoreCase(id)){
                FavouriteRoom favouriteRoom = new FavouriteRoom(tenantId,room.getId());
                Data.favouriteRooms.add(favouriteRoom);
                existedRooms = true;
            }
        }
        if(existedRooms){
            System.out.println("Đã thêm phòng vào danh sách yêu thích");
        }
        else{
            System.out.println("Không tìm thấy phòng theo ID bạn yêu cầu.");
        }

    }
    public void displayFavouriteList(){
        System.out.println("=============================== Danh sách phòng trọ ==============================================================");
        System.out.printf("%-10s %-20s %-30s %-15s %-15s %-10s \n",
                "ID", "Mô tả", "Vị trí", "Loại phòng", "Giá", "Trạng thái");
        System.out.println("==================================================================================================================");

        for (FavouriteRoom favouriteRoom: Data.favouriteRooms) {
            Room room = roomService.findRoomById(favouriteRoom.getRoomId());
                String roomStatus = (room.getRoomStatus() != null) ? room.getRoomStatus().toString() : "N/A";

                // Giới hạn mô tả tối đa 20 ký tự
                String description = room.getDescription();
                if (description.length() > 20) {
                    description = description.substring(0, 17) + "...";
                }

                System.out.printf("%-10s %-20s %-30s %-15s %-15s %-10s\n",
                        room.getId(),
                        room.getDescription(),
                        room.getLocation(),
                        room.getPropertyType(),
                        room.getPrice().toString(),
                        roomStatus
                );
            }
        System.out.println("==================================================================================================================");

    }



   public void displayRefusedRequest(int tenantId){
        System.out.println("=================Yêu cầu thuê phòng bị từ chôi==============================");
        System.out.printf("%-3s \t %-15s \t %-30s %-10s \n", "ID", "ID người thuê", "Id phòng muốn thuê", "Trạng thái");
        System.out.println("============================================================================");
        for (RentalRequest rentalRequest : Data.rentalRequests) {
            if (rentalRequest.getTenantId() == tenantId && rentalRequest.getStatus() == RentalRequestStatus.REJECTED) {
                System.out.printf("%-3s \t %-15s \t %-30s %-10s \n", rentalRequest.getId(), rentalRequest.getTenantId(), rentalRequest.getRoomId(), rentalRequest.getStatus());
            }
        }
    }
    public Contract findPendingContractByTenantId(User user, Scanner scanner){
        Menu menu =Menu.getInstance();
        for (Contract contract: Data.contracts) {
            if(contract.getTenantId() == user.getId() && contract.getContractStatus() == ContractStatus.PENDING){
                return contract;
            }
        }
        System.out.println("Không tìm thấy hợp đồng với Id bạn chọn.");
        menu.selectMenuTenant(scanner, user);
        return null;
    }

    public void findStatusConTractsByTenantId(int tenantId, ContractStatus status){
        boolean found = false; // Biến cờ để kiểm tra xem có hợp đồng nào phù hợp không
        for (Contract contract: Data.contracts) {
            if(contract.getTenantId() == tenantId && contract.getContractStatus() == status){
                contractService.formatContract(contract.getId());
                found = true;
            }
        }
        if(!found){
            System.out.println("Không tìm thấy hợp đồng với trạng thái " + status + " cho người thuê có ID: " + tenantId);
        }
    }
    public void requestToRentRoom(Scanner scanner,int tenantId){
        System.out.println("Mời bạn nhập các thông tin sau");
        Room existedRoom;
        String roomId;
        LocalDate startDate,endDate;
        RentalRequest isValidRequest;

       do{
           System.out.println("Mời bạn nhập id căn phòng muốn thuê");
           roomId = scanner.nextLine();
           isValidRequest = rentalRequestService.checkTenantRequest(tenantId,roomId);
           existedRoom = roomService.findRoomInAvaiableStatusById(roomId);
           if(existedRoom == null){
               System.out.println("Căn phòng bạn tìm kiếm đã được cho thuê/đặt trước hoặc id bạn nhập vào không hợp lệ.");
           }else if(isValidRequest != null){
               System.out.println("Bạn đã gửi yêu cầu thuê căn phòng này trước đó.");
           }
       }while(existedRoom == null || isValidRequest != null ); // Check xem người thuê này đã gửi yêu câu thuê phòng trước đó hay chưa.
        do {
            startDate = Utils.checkDateValidate(scanner);
            endDate = Utils.checkDateValidate2(scanner);

            // Kiểm tra ngày bắt đầu phải trước ngày kết thúc
            if (endDate.isBefore(startDate)) {
                System.out.println("Ngày kết thúc phải sau ngày bắt đầu. Vui lòng nhập lại.");
            }
        } while (endDate.isBefore(startDate));  // Lặp lại cho đến khi ngày kết thúc hợp lệ

        RentalRequest rentalRequest = new RentalRequest(tenantId, roomId,startDate,endDate);
        Data.rentalRequests.add(rentalRequest);
        System.out.println("Yêu cầu thuê phòng đã được gửi đến cho chủ phòng.");
    }
    public void displayContractAfterLandLordApproved(User user,Scanner scanner){
        Menu menu =Menu.getInstance();
        Contract contract = findPendingContractByTenantId(user,scanner);
        // Format và hiển thị hợp đồng
        contractService.formatContract(contract.getId());
        Contract existedContract;
        // Vòng lặp tìm hợp đồng với ID cụ thể từ người dùng nhập
        do{
            System.out.println("Nhập id hợp đồng mà bạn muốn ký kết");
            String contractId = scanner.nextLine();
            existedContract = contractService.findPendingContractById(contractId);
        }while (existedContract == null);
        // Xử lý quyết định ký hợp đồng
        boolean validInput = false;
        do{
            System.out.println("Bạn có muốn đồng ý ký kết hợp đồng này không?(Y/N)");
            String choice = scanner.nextLine();
            if(choice.equalsIgnoreCase("Y")){
                existedContract.setContractStatus(ContractStatus.SIGNED);
                // Sau khi hợp đồng được kí kết phòng sẽ chuyển trạng thái đặt trước sang đã được thuê
                Room room = roomService.findRoomById(existedContract.getRoomId());
                room.setRoomStatus(RoomStatus.RENTED);
                System.out.println("Kí hợp đồng thành công.");
                Data.contracts.add(existedContract);
                validInput = true;
            }else if(choice.equalsIgnoreCase("N")) {
                existedContract.setContractStatus(ContractStatus.REJECTED);
                validInput = true;
            }else {
                System.out.println("Lựa chọn không hợp lệ, vui lòng nhập lại.");
            }
        }while (!validInput);
        // Sau khi xử lý xong, quay lại menu cho người thuê
        menu.selectMenuTenant(scanner, user);

    }
    public void cancelContract(Scanner scanner, User user){
        Menu menu =Menu.getInstance();
        Contract cancelContract;
        do{
            System.out.println("Mời bạn nhập vào ID hợp đồng muốn hủy");
            String contractId = scanner.nextLine();
            cancelContract = contractService.findSignedContractById(contractId);
        }while(cancelContract == null);
        System.out.println("Bạn có muốn thật sự hủy hợp đồng này không?(Y/N))");
        String choice = scanner.nextLine();
        if(choice.equalsIgnoreCase("Y")){
            cancelContract.setContractStatus(ContractStatus.PENDINGCANCEL);
            System.out.println("Yêu cầu hủy sẽ được chuyển tới cho chủ trọ.");
        }else{
            menu.selectMenuTenant(scanner,user);
        }
    }
}
