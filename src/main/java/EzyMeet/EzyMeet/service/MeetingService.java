package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.dto.*;
import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;

import java.util.List;

public interface MeetingService {
    ResponseMeetingDto createMeeting(RequestCreateMeetingDto requestDto);

    List<ResponseMeetingDto> getUserMeetings(String userId);

    ResponseMeetingInfoDto getSingleMeetingById(String meetingId);

    ResponseMeetingDto updateMeeting(String meetingId, RequestUpdateMeetingDto requestUpdateDto);

    ResponseMeetingDto deleteMeeting(String meetingId);
}