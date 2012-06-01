/*
    Copyright 2011 Josh Drummond

    This file is part of WebPasswordSafe.

    WebPasswordSafe is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    WebPasswordSafe is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with WebPasswordSafe; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package net.webpasswordsafe.common.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import net.sf.gilead.pojo.gwt.LightEntity;


/**
 * Domain model POJO for an ip_lockout
 * 
 * @author Josh Drummond
 *
 */
@Entity
@Table(name="ip_lockout")
public class IPLockout extends LightEntity implements Serializable
{
    private static final long serialVersionUID = 6584879769103190005L;
    public static final int LENGTH_IPADDRESS = 50;

    @Id
    @GeneratedValue
    @Column(name="id")
    private long id;
    
    @Column(name="ipaddress", length=LENGTH_IPADDRESS, nullable=false, updatable=false, unique=true)
    private String ipaddress;

    @Column(name="fail_count", nullable=false)
    private int failCount;

    @Column(name="lockout_date")
    private Date lockoutDate;
    
    public IPLockout()
    {
        this(null, 0);
    }

    public IPLockout(String ipaddress, int failCount)
    {
        this.ipaddress = ipaddress;
        this.failCount = failCount;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getIpaddress()
    {
        return ipaddress;
    }

    public void setIpaddress(String ipaddress)
    {
        this.ipaddress = ipaddress;
    }

    public int getFailCount()
    {
        return failCount;
    }

    public void setFailCount(int failCount)
    {
        this.failCount = failCount;
    }

    public Date getLockoutDate()
    {
        return lockoutDate;
    }

    public void setLockoutDate(Date lockoutDate)
    {
        this.lockoutDate = lockoutDate;
    }

}
