package EzyMeet.EzyMeet.service;

import EzyMeet.EzyMeet.dto.RequestCreateMeetingDto;
import EzyMeet.EzyMeet.dto.RequestUpdateMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseDetailedMeetingDto;
import EzyMeet.EzyMeet.dto.ResponseMeetingDto;
import EzyMeet.EzyMeet.model.Meeting;
import EzyMeet.EzyMeet.model.MeetingParticipant;

import java.util.List;

public interface MeetingService {
    ResponseMeetingDto createMeeting(RequestCreateMeetingDto requestDto);

    List<ResponseMeetingDto> getUserMeetings(String userId);

    ResponseDetailedMeetingDto getSingleMeetingById(String meetingId);

    ResponseMeetingDto updateMeeting(String meetingId, RequestUpdateMeetingDto requestUpdateDto);

    ResponseMeetingDto deleteMeeting(String meetingId);
}