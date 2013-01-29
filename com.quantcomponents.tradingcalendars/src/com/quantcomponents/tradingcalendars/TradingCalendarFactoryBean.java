/*******************************************************************************
 * Copyright (c) 2013 Luigi Sgro. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Luigi Sgro - initial API and implementation
 ******************************************************************************/

package com.quantcomponents.tradingcalendars;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.quantcomponents.core.calendar.AbstractPeriodicTradingCalendar;
import com.quantcomponents.core.calendar.ITradingCalendar;
import com.quantcomponents.core.calendar.ITradingDay;
import com.quantcomponents.core.model.BareDate;
import com.quantcomponents.core.xml.XmlDateAdapter;

@XmlRootElement(name = "tradingCalendar")
public class TradingCalendarFactoryBean implements Serializable {
	
	public static class WeekTradingDay {
		public WeekTradingDay() {}
		public WeekTradingDay(Integer dayOfTheWeek, TradingDayBean tradingDay) {
			this.dayOfTheWeek = dayOfTheWeek;
			this.tradingDay = tradingDay;
		}
		private Integer dayOfTheWeek;
		private TradingDayBean tradingDay;
		public Integer getDayOfTheWeek() {
			return dayOfTheWeek;
		}
		public void setDayOfTheWeek(Integer dayOfTheWeek) {
			this.dayOfTheWeek = dayOfTheWeek;
		}
		public TradingDayBean getTradingDay() {
			return tradingDay;
		}
		public void setTradingDay(TradingDayBean tradingDay) {
			this.tradingDay = tradingDay;
		}
		
	}
	
	public static class SpecificTradingDay {
		public SpecificTradingDay() {}
		public SpecificTradingDay(String date, TradingDayBean tradingDay) {
			this.date = date;
			this.tradingDay = tradingDay;
		}
		private String date;
		private TradingDayBean tradingDay;
		public String getDate() {
			return date;
		}
		public void setDate(String date) {
			this.date = date;
		}
		public TradingDayBean getTradingDay() {
			return tradingDay;
		}
		public void setTradingDay(TradingDayBean tradingDay) {
			this.tradingDay = tradingDay;
		}
	}
	
	private static final long serialVersionUID = -1876628600287301654L;
	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private String timeZoneId;
	private Set<BareDate> closingDays;
	private Set<WeekTradingDay> weekTradingDaysSet;
	private Set<SpecificTradingDay> specialTradingDaysSet;
	private Boolean includeAfterHours;
	private String[] exchanges;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean isIncludeAfterHours() {
		return includeAfterHours;
	}

	public void setIncludeAfterHours(Boolean includeAfterHours) {
		this.includeAfterHours = includeAfterHours;
	}

	public String[] getExchanges() {
		return exchanges;
	}

	@XmlElementWrapper
	@XmlElement(name = "exchange")
	public void setExchanges(String[] exchanges) {
		this.exchanges = exchanges;
	}

	@XmlElement(name = "startDate")
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@XmlElement(name = "endDate")
	@XmlJavaTypeAdapter(XmlDateAdapter.class)
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@XmlElement(name = "timeZone")
	public void setTimeZoneId(String timeZoneId) {
		this.timeZoneId = timeZoneId;
	}

	@XmlElementWrapper
	@XmlElement(name = "closingDay")
	public void setClosingDays(Set<BareDate> closingDays) {
		this.closingDays = closingDays;
	}

	@XmlElementWrapper
	@XmlElement(name = "weekTradingDay")
	public void setWeekTradingDays(Set<WeekTradingDay> weekTradingDaysSet) {
		this.weekTradingDaysSet = weekTradingDaysSet;
	}

	@XmlElementWrapper
	@XmlElement(name = "specialTradingDay")
	public void setSpecialTradingDays(Set<SpecificTradingDay> specialTradingDaysSet) {
		this.specialTradingDaysSet = specialTradingDaysSet;
	}

	public String getTimeZoneId() {
		return timeZoneId;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone(timeZoneId);
	}

	public Set<BareDate> getClosingDays() {
		return closingDays;
	}

	public Set<WeekTradingDay> getWeekTradingDays() {
		return weekTradingDaysSet;
	}
	
	public Map<Integer, TradingDayBean> getWeekTradingDaysAsMap() {
		Map<Integer, TradingDayBean> map = new HashMap<Integer, TradingDayBean>();
		for (WeekTradingDay wtd : getWeekTradingDays()) {
			map.put(wtd.dayOfTheWeek, wtd.tradingDay);
		}
		return map;
	}
	
	public Set<SpecificTradingDay> getSpecialTradingDays() {
		return specialTradingDaysSet;
	}
	
	public Map<String, TradingDayBean> getSpecialTradingDaysAsMap() {
		Map<String, TradingDayBean> map = new HashMap<String, TradingDayBean>();
		for (SpecificTradingDay std : getSpecialTradingDays()) {
			map.put(std.date, std.tradingDay);
		}
		return map;
	}

	public ITradingCalendar createTradingCalendar() {
		return new AbstractPeriodicTradingCalendar() {
			private final String name = TradingCalendarFactoryBean.this.name;
			private final String description = TradingCalendarFactoryBean.this.description;
			private final Date startDate = TradingCalendarFactoryBean.this.startDate;
			private final Date endDate = TradingCalendarFactoryBean.this.endDate;
			private final TimeZone timeZone = TimeZone.getTimeZone(timeZoneId);
			private final Set<BareDate> closingDays = TradingCalendarFactoryBean.this.closingDays == null ?
					Collections.unmodifiableSet(new HashSet<BareDate>()) :
					Collections.unmodifiableSet(new HashSet<BareDate>(TradingCalendarFactoryBean.this.closingDays));
			private final Map<Integer, TradingDayBean> weekTradingDays = Collections.unmodifiableMap(TradingCalendarFactoryBean.this.getWeekTradingDaysAsMap());
			private final Map<String, TradingDayBean> specialTradingDays = Collections.unmodifiableMap(TradingCalendarFactoryBean.this.getSpecialTradingDaysAsMap());
			@Override
			public String getName() {
				return name;
			}
			@Override
			public String getDescription() {
				return description;
			}
			@Override
			public Date getStartDate() {
				return startDate;
			}
			@Override
			public Date getEndDate() {
				return endDate;
			}
			@Override
			public TimeZone getTimeZone() {
				return timeZone;
			}
			@Override
			protected Set<BareDate> getClosingDays() {
				return closingDays;
			}
			@Override
			protected Map<String, ? extends ITradingDay> getSpecialTradingDays() {
				return specialTradingDays;
			}
			@Override
			protected Map<Integer, ? extends ITradingDay> getWeekTradingDays() {
				return weekTradingDays;
			}
			@Override
			public Boolean isIncludeAfterHours() {
				return includeAfterHours;
			}
			@Override
			public String[] getExchanges() {
				return exchanges;
			}
		};
	}
}
