package ecnu.edu.iotbackend.entity;

import java.util.ArrayList;
import java.util.List;

public class UserRoomAssignmentRequest {

    private Integer userId;
    private List<Integer> roomIds = new ArrayList<>();

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Integer> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(List<Integer> roomIds) {
        this.roomIds = roomIds;
    }
}
