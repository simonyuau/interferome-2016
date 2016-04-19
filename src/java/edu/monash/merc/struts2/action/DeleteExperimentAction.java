/*
 * Copyright (c) 2010-2011, Monash e-Research Centre
 * (Monash University, Australia)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 	* Redistributions of source code must retain the above copyright
 * 	  notice, this list of conditions and the following disclaimer.
 * 	* Redistributions in binary form must reproduce the above copyright
 * 	  notice, this list of conditions and the following disclaimer in the
 * 	  documentation and/or other materials provided with the distribution.
 * 	* Neither the name of the Monash University nor the names of its
 * 	  contributors may be used to endorse or promote products derived from
 * 	  this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package edu.monash.merc.struts2.action;

import edu.monash.merc.config.AppPropSettings;
import edu.monash.merc.domain.AuditEvent;
import edu.monash.merc.domain.Experiment;
import edu.monash.merc.domain.User;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.GregorianCalendar;

/**
 * DeleteExperimentAction Action class
 *
 * @author Simon Yu - Xiaoming.Yu@monash.edu
 * @version 2.0
 */
@Scope("prototype")
@Controller("data.deleteExpAction")
public class DeleteExperimentAction extends DMBaseAction {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    public String deleteMyExp() {
        try {
            if (!verifyDelExpParams()) {
                setNavAndTitleForMyExpExc();
                return ERROR;
            }

            experiment = this.dmService.getExperimentById(experiment.getId());
            // if experiment not found.
            if (experiment == null) {
                logger.error(getText("experiment.delete.experiment.not.found"));
                addActionError(getText("experiment.delete.experiment.not.found"));
                setNavAndTitleForMyExpExc();
                return ERROR;
            }
            // check the permissions for this experiment
            checkExpPermsForUser(experiment.getId(), experiment.getOwner().getId());
            if (!permissionBean.isDeleteAllowed()) {
                addActionError(getText("experiment.delete.experiment.permission.denied"));
                setNavAndTitleForMyExpExc();
                return ERROR;
            }

            if (experiment.isMdPublished()) {
                String rifcsRootPath = appSetting.getPropValue(AppPropSettings.ANDS_RIFCS_STORE_LOCATION);
                this.dmService.deletePublishedExperiment(experiment, rifcsRootPath);
            } else {
                this.dmService.deleteExperiment(experiment);
            }
            setSuccessActMsg(getText("experiment.delete.experiment.success.message", new String[]{this.namePrefix + experiment.getId()}));
            // set the audit event
            recordAuditEventForDelete(experiment, user);
            // set the navigation and page title
            setNavAndTitleForMyExp();

        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("experiment.delete.experiment.failed"));
            setNavAndTitleForMyExpExc();
            return ERROR;
        }
        return SUCCESS;
    }

    public String deleteExp() {
        try {
            if (!verifyDelExpParams()) {
                setNavAndTitleForMyExpExc();
                return ERROR;
            }

            experiment = this.dmService.getExperimentById(experiment.getId());
            // if experiment not found.
            if (experiment == null) {
                logger.error(getText("experiment.delete.experiment.not.found"));
                addActionError(getText("experiment.delete.experiment.not.found"));
                setNavAndTitleForExpExc();
                return ERROR;
            }
            // check the permissions for this experiment
            checkExpPermsForUser(experiment.getId(), experiment.getOwner().getId());
            if (!permissionBean.isDeleteAllowed()) {
                addActionError(getText("experiment.delete.experiment.permission.denied"));
                setNavAndTitleForExpExc();
                return ERROR;
            }

            if (experiment.isMdPublished()) {
                String rifcsRootPath = appSetting.getPropValue(AppPropSettings.ANDS_RIFCS_STORE_LOCATION);
                this.dmService.deletePublishedExperiment(experiment, rifcsRootPath);
            } else {
                this.dmService.deleteExperiment(experiment);
            }
            // set the success message.
            setSuccessActMsg(getText("experiment.delete.experiment.success.message", new String[]{this.namePrefix + experiment.getId()}));
            // set the audit event
            recordAuditEventForDelete(experiment, user);
            // set the navigation and page title
            setNavAndTitleForExp();

        } catch (Exception e) {
            logger.error(e);
            addActionError(getText("experiment.delete.experiment.failed"));
            setNavAndTitleForExpExc();
            return ERROR;
        }
        return SUCCESS;
    }

    private boolean verifyDelExpParams() {
        if (experiment == null || (experiment != null && experiment.getId() == 0)) {
            addFieldError("experiment.id", getText("experiment.delete.experiment.id.must.be.provided"));
            return false;
        } else {
            return true;
        }
    }

    // record the auditing event
    private void recordAuditEventForDelete(Experiment exp, User operator) {
        AuditEvent ev = new AuditEvent();
        ev.setCreatedTime(GregorianCalendar.getInstance().getTime());
        ev.setEvent(getText("experiment.delete.experiment.success.message", new String[]{this.namePrefix + exp.getId()}));
        ev.setEventOwner(exp.getOwner());
        ev.setOperator(operator);
        recordActionAuditEvent(ev);
    }

    // for my experiment
    private void setNavAndTitleForMyExp() {
        setPageTitle(getText("experiment.delete.my.experiment.action.title"));
        String secondNav = getText("experiment.list.my.all.experiments.action.title");
        String secondNavLink = "data/listMyExperiments.jspx";
        String thirdNav = this.namePrefix + experiment.getId();
        navBar = createNavBar("Experiment", null, secondNav, secondNavLink, thirdNav, null);
    }

    // for my experiment
    private void setNavAndTitleForMyExpExc() {
        setPageTitle(getText("experiment.delete.my.experiment.action.title"));
        String secondNav = getText("experiment.list.my.all.experiments.action.title");
        String secondNavLink = "data/listMyExperiments.jspx";
        String thirdNav = getText("experiment.delete.my.experiment.action.title");
        navBar = createNavBar("Experiment", null, secondNav, secondNavLink, thirdNav, null);
    }

    // for experiemnt
    private void setNavAndTitleForExp() {
        setPageTitle(getText("experiment.delete.experiment.action.title"));
        String secondNav = getText("experiment.list.all.experiments.action.title");
        String secondNavLink = "data/listExperiments.jspx";
        String thirdNav = this.namePrefix + experiment.getId();
        navBar = createNavBar("Experiment", null, secondNav, secondNavLink, thirdNav, null);
    }

    // for experiment
    private void setNavAndTitleForExpExc() {
        setPageTitle(getText("experiment.delete.experiment.action.title"));
        String secondNav = getText("experiment.list.all.experiments.action.title");
        String secondNavLink = "data/listExperiments.jspx";
        String thirdNav = getText("experiment.delete.experiment.action.title");
        navBar = createNavBar("Experiment", null, secondNav, secondNavLink, thirdNav, null);
    }
}
