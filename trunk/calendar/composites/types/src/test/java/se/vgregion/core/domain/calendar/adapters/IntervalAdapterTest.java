/**
 * Copyright 2010 Västra Götalandsregionen
 *
 *   This library is free software; you can redistribute it and/or modify
 *   it under the terms of version 2.1 of the GNU Lesser General Public
 *   License as published by the Free Software Foundation.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the
 *   Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *   Boston, MA 02111-1307  USA
 *
 */

/**
 * 
 */
package se.vgregion.core.domain.calendar.adapters;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import se.vgregion.core.domain.calendar.CalendarItemPeriod;

/**
 * @author Anders Asplund
 * 
 */
public class IntervalAdapterTest {

    private IntervalAdapter adapter = new IntervalAdapter();
    private Interval intervalMock = new Interval(null);
    @Mock
    CalendarItemPeriod period;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#marshal(org.joda.time.Interval)}.
     */
    @Test(expected = UnsupportedOperationException.class)
    public final void marshalShouldBeUnsupported() throws Exception {
        adapter.marshal(intervalMock);
    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test(expected = VgrCalendarWebServiceException.class)
    public final void unmarshalShouldThrowVgrCalendarWebServiceExceptionIfStartDateIsNull() throws Exception {
        // Given
        given(period.getStartDate()).willReturn(null);
        given(period.getEndDate()).willReturn("2010-10-01");

        // When
        adapter.unmarshal(period);

    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test(expected = VgrCalendarWebServiceException.class)
    @Ignore
    public final void unmarshalShouldThrowVgrCalendarWebServiceExceptionIfStartDateIsEmpty() throws Exception {
        // Given
        given(period.getStartDate()).willReturn("");
        given(period.getEndDate()).willReturn("2010-10-01");

        // When
        adapter.unmarshal(period);

    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test(expected = VgrCalendarWebServiceException.class)
    public final void unmarshalShouldThrowVgrCalendarWebServiceExceptionIfEndDateIsNull() throws Exception {
        // Given
        given(period.getStartDate()).willReturn("2010-10-01");
        given(period.getEndDate()).willReturn(null);

        // When
        adapter.unmarshal(period);

    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test(expected = VgrCalendarWebServiceException.class)
    public final void unmarshalShouldThrowVgrCalendarWebServiceExceptionIfEndDateIsEmpty() throws Exception {
        // Given
        given(period.getStartDate()).willReturn("2010-10-01");
        given(period.getEndDate()).willReturn("");

        // When
        adapter.unmarshal(period);

    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test
    @Ignore
    public void unmarshalShouldReturnAnIntervalInstance() throws Exception {
        // Given
        given(period.getStartDate()).willReturn("2010-10-01");
        given(period.getStartTime()).willReturn("12:00:00");
        given(period.getEndDate()).willReturn("2010-10-01");
        given(period.getEndTime()).willReturn("13:00:00");

        // When
        Interval interval = adapter.unmarshal(period);

        // Then
        assertNotNull(interval);
    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test
    public void unmarshalShouldReturnAnIntervalInstanceEvenIfNoTimeIsSpecified() throws Exception {
        // Given
        given(period.getStartDate()).willReturn("2010-10-01");
        given(period.getStartTime()).willReturn(null);
        given(period.getEndDate()).willReturn("2010-10-01");
        given(period.getEndTime()).willReturn("");

        // When
        Interval interval = adapter.unmarshal(period);

        // Then
        assertNotNull(interval);
    }

    /**
     * Test method for
     * {@link se.vgregion.core.domain.calendar.adapters.IntervalAdapter#unmarshal(org.joda.time.Interval)} .
     */
    @Test(expected = VgrCalendarWebServiceException.class)
    public void unmarshalShouldThrowVgrCalendarWebServiceExceptionIfEndDateIsEndIsBeforStart() throws Exception {
        // Given
        given(period.getStartDate()).willReturn("2010-10-01");
        given(period.getStartTime()).willReturn("12:00:00");
        given(period.getEndDate()).willReturn("2010-10-01");
        given(period.getEndTime()).willReturn("");

        // When
        adapter.unmarshal(period);
    }
}
