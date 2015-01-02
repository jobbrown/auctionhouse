package com.jobbrown.auction_room.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jobbrown.auction_room.models.Notification;

public class NotificationList {
	private ArrayList<Notification> notificationList = new ArrayList<Notification>();
	
	public NotificationList() {
		JavaSpacesNotificationService ns = new JavaSpacesNotificationService();
		this.notificationList = ns.getAllNotifications();
	}
	
	public NotificationList(ArrayList<Notification> list) {
		this.notificationList = list;
	}
	
	public NotificationList read() {
		ArrayList<Notification> returnable = new ArrayList<Notification>();
		
		for(Notification n : notificationList) {
			if(n.read) {
				returnable.add(n);
			}
		}
		
		return new NotificationList(returnable);
	}
	
	public NotificationList unread() {
		ArrayList<Notification> returnable = new ArrayList<Notification>();
		
		for(Notification n : notificationList) {
			if(!n.read) {
				returnable.add(n);
			}
		}
		
		return new NotificationList(returnable);
	}
	
	/**
	 * Sort the notification list by old -> new
	 * @return NotificationList
	 */
	public NotificationList sortByOldest() {
		ArrayList<Notification> notificationCopy = new ArrayList<Notification>(notificationList);
		
		Collections.sort(notificationCopy, new Comparator<Notification>() {
			@Override
			public int compare(Notification notification, Notification notification2) {
				return notification.datetime.compareTo(notification2.datetime);
			}
		});
		
		return new NotificationList(notificationCopy);
	}
	
	/**
	 * Sort the notification list by new -> old
	 * @return NotificationList
	 */
	public NotificationList sortByNewest() {
		ArrayList<Notification> notificationCopy = new ArrayList<Notification>(notificationList);
		
		Collections.sort(notificationCopy, new Comparator<Notification>() {
			@Override
			public int compare(Notification notification, Notification notification2) {
				return notification2.datetime.compareTo(notification.datetime);
			}
		});
		
		return new NotificationList(notificationCopy);
	}
	
	public Notification one() {
		if(count() == 0) {
			return null;
		}
		
		return notificationList.get(0);
	}
	
	public ArrayList<Notification> all() {
		return notificationList;
	}
	
	public int count() {
		return notificationList.size();
	}
}
