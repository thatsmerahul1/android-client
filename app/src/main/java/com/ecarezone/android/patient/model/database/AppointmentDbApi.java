package com.ecarezone.android.patient.model.database;

import android.app.UiAutomation;
import android.content.Context;

import com.ecarezone.android.patient.model.Appointment;
import com.ecarezone.android.patient.model.Chat;
import com.ecarezone.android.patient.utils.Util;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 10603675 on 25-05-2016.
 */
public class AppointmentDbApi {

    private static DbHelper mDbHelper;
    private static AppointmentDbApi mAppointmentDbapi;
    private static Context mContext;

    private AppointmentDbApi() {

    }

    public static AppointmentDbApi getInstance(Context context) {
        mContext = context;
        if (mDbHelper == null || mAppointmentDbapi == null) {
            mDbHelper = new DbHelper(context);
            mAppointmentDbapi = new AppointmentDbApi();
        }
        return mAppointmentDbapi;

    }

    /* Saves a appointment in local database. Returns success failure response. */
    public boolean saveAppointment(Appointment appointment) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            if(isAppointmentPresent(appointment.getAppointmentId())){
                deleteAppointment(appointment);
            }
            int status = appointmentDao.create(appointment);
            return status != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /* retrieve the AppointmentHistory of a particular user */
    public List<Appointment> getAppointmentHistory(String patientId) {
        try {
            Dao<Appointment, Integer> chatDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = chatDao.queryBuilder();
            return queryBuilder.where().eq(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, patientId).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* update read status of user Chat history */
    public boolean updateAppointment(String patientId, String callType, String doctorId, long dateTime,
                                     String appointmentId) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            UpdateBuilder<Appointment, Integer> updateBuilder = appointmentDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, patientId);
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_CALL_TYPE, callType);
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, doctorId);
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DATE_TIME, dateTime);

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAppointment(long appointmentId, Appointment appointment) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            UpdateBuilder<Appointment, Integer> updateBuilder = appointmentDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, appointment.getpatientId());
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_CALL_TYPE, appointment.getCallType());
            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DATE_TIME, appointment.getTimeStamp());

            updateBuilder.update();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAppointmentStatus(long appointmentId, boolean isConfirmed) {

        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            UpdateBuilder<Appointment, Integer> updateBuilder = appointmentDao.updateBuilder();
            updateBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

            updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, isConfirmed);
            int isUpdated = updateBuilder.update();
            if (isUpdated > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    public boolean updateOrInsertAppointment(Appointment appointment) {

        long appointmentId = appointment.getAppointmentId();
        List<Appointment> appointmentList = getAppointment(appointmentId);
        if (appointmentList == null || appointmentList.size() == 0) {
//            Appointment does not exist..
            saveAppointment(appointment);
        } else {
//            Update the existing appointment...
            try {
                Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
                UpdateBuilder<Appointment, Integer> updateBuilder = appointmentDao.updateBuilder();
                updateBuilder.where()
                        .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

                updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DATE_TIME, appointment.getTimeStamp());
                updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_CALL_TYPE, appointment.getCallType());
                updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, appointment.getDoctorId());
                updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, appointment.getpatientId());
                updateBuilder.updateColumnValue(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, appointment.isConfirmed());
                int isUpdated = updateBuilder.update();
                if (isUpdated > 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public List<Appointment> getAppointment(long appointmentId) {

        List<Appointment> appointmentList = null;

        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();
            queryBuilder.where()
                    .gt(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

            appointmentList = queryBuilder.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentList;

    }

    public List<Appointment> getAppointmentHistory(long doctorId, long patientId, Date startDate) {

        List<Appointment> appointmentList = null;
        List<Appointment> retAppointmentList = new ArrayList<Appointment>();
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();
            queryBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, String.valueOf(patientId))
                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, String.valueOf(doctorId));
//                            .and()
//                            .gt(DbContract.Appointments.COLUMN_NAME_DATE_TIME, startDate.getTime());
            queryBuilder.orderBy(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, true);
            appointmentList = queryBuilder.query();
            if (appointmentList != null) {
                for (Appointment appointment : appointmentList) {
                    long appointmentTime = Util.getTimeInLongFormat(appointment.getTimeStamp());
                    long appointmentEndTime = appointmentTime + 30 * 60 * 1000;

                    if (/*startDate.getTime() < appointmentTime && */startDate.getTime() < appointmentEndTime) {
                        appointment.setDateTimeInLong(appointmentTime);
                        retAppointmentList.add(appointment);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.sort(retAppointmentList);
        return retAppointmentList;
    }

    /* retrieve appointments from Appointment table by patientId */
    public List<Appointment> getAppointments(String doctorId, boolean isConfirmed) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();

            return queryBuilder.where()
//                    .eq(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, patientId)
//                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_DOCTOR_ID, doctorId)
                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, isConfirmed)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAppointmentsByPatientId(String patientId, boolean isConfirmed) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();

            return queryBuilder.where()
//                    .eq(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, patientId)
//                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_PATIENT_ID, patientId)
                    .and()
                    .eq(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, isConfirmed)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Appointment> getAllPendingAppointments() {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();
            queryBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, false);
            List<Appointment> appointmentList = queryBuilder.query();
            return appointmentList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Appointment> getAllPendingAppointments(Date currentTime) {
        List<Appointment> appointmentList = null;
        List<Appointment> retAppointmentList = new ArrayList<Appointment>();
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();
            queryBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_IS_CONFIRMED, false);
            queryBuilder.orderBy(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, true);
            appointmentList = queryBuilder.query();
            if (appointmentList != null) {
                for (Appointment appointment : appointmentList) {
                    long appointmentTime = Util.getTimeInLongFormat(appointment.getTimeStamp());

                    if (currentTime.getTime() < appointmentTime) {
                        appointment.setDateTimeInLong(appointmentTime);
                        retAppointmentList.add(appointment);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        Collections.sort(retAppointmentList);
        return retAppointmentList;
    }
    public List<Appointment> getAllAppointments() {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();
            List<Appointment> appointmentList = queryBuilder.query();
            return appointmentList;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param currentAppointment
     * @return
     */
    public boolean deleteAppointment(Appointment currentAppointment) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            DeleteBuilder<Appointment, Integer> deleteBuilder = appointmentDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, currentAppointment.getAppointmentId());
            int count = deleteBuilder.delete();
            return count > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param appointmentId
     * @return
     */
    public boolean deleteAppointment(long appointmentId) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            DeleteBuilder<Appointment, Integer> deleteBuilder = appointmentDao.deleteBuilder();
            deleteBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);
            int count = deleteBuilder.delete();
            return count > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * @param appointmentId
     * @return
     */
    public boolean isAppointmentPresent(long appointmentId) {
        try {
            Dao<Appointment, Integer> appointmentDao = mDbHelper.getAppointmentDao();
            QueryBuilder<Appointment, Integer> queryBuilder = appointmentDao.queryBuilder();

            queryBuilder.where()
                    .eq(DbContract.Appointments.COLUMN_NAME_APPOINTMENT_ID, appointmentId);

            List<Appointment> appointmentList = queryBuilder.query();
            return appointmentList.size() > 0 ? true : false;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
