/**
 * IT Ticket System JS SDK
 * A lightweight JavaScript SDK for embedding IT ticket submission forms
 * into third-party applications.
 * 
 * Version: 1.0.0
 * 
 * Usage:
 *   <script src="https://your-domain.com/it-ticket-sdk.js"></script>
 *   <script>
 *     ITTicketSDK.init({
 *       apiUrl: 'https://your-domain.com/api',
 *       token: 'your-jwt-token'
 *     });
 *     
 *     // Open ticket submission form
 *     ITTicketSDK.openTicketForm();
 *     
 *     // Create ticket programmatically
 *     ITTicketSDK.createTicket({
 *       title: 'Bug Report',
 *       description: 'Description here',
 *       type: 'BUG',
 *       priority: 'HIGH'
 *     });
 *   </script>
 */

(function(global) {
  'use strict';

  var ITTicketSDK = {
    version: '1.0.0',
    config: {
      apiUrl: '',
      token: '',
      theme: 'light',
      language: 'zh-CN'
    },
    
    /**
     * Initialize the SDK with configuration
     * @param {Object} options - Configuration options
     * @param {string} options.apiUrl - API base URL
     * @param {string} options.token - JWT authentication token
     * @param {string} options.theme - Theme ('light' or 'dark')
     * @param {string} options.language - Language code
     */
    init: function(options) {
      if (!options || !options.apiUrl) {
        console.error('ITTicketSDK: apiUrl is required');
        return;
      }
      
      this.config = Object.assign({}, this.config, options);
      this._injectStyles();
      console.log('ITTicketSDK initialized successfully');
    },
    
    /**
     * Set authentication token
     * @param {string} token - JWT token
     */
    setToken: function(token) {
      this.config.token = token;
    },
    
    /**
     * Open ticket submission form in a modal
     * @param {Object} options - Form options
     */
    openTicketForm: function(options) {
      options = options || {};
      var self = this;
      
      var modal = document.createElement('div');
      modal.id = 'it-ticket-modal';
      modal.innerHTML = this._getModalHTML(options);
      
      document.body.appendChild(modal);
      
      // Bind events
      modal.querySelector('.it-ticket-close').addEventListener('click', function() {
        self.closeTicketForm();
      });
      
      modal.querySelector('.it-ticket-submit').addEventListener('click', function() {
        self._handleSubmit(options.onSubmit);
      });
      
      modal.querySelector('.it-ticket-overlay').addEventListener('click', function() {
        self.closeTicketForm();
      });
    },
    
    /**
     * Close ticket submission form
     */
    closeTicketForm: function() {
      var modal = document.getElementById('it-ticket-modal');
      if (modal) {
        modal.remove();
      }
    },
    
    /**
     * Create a ticket programmatically
     * @param {Object} ticket - Ticket data
     * @param {string} ticket.title - Ticket title
     * @param {string} ticket.description - Ticket description
     * @param {string} ticket.type - Ticket type (BUG, FEATURE, TASK, SUPPORT, OTHER)
     * @param {string} ticket.priority - Priority (LOW, MEDIUM, HIGH, CRITICAL)
     * @param {number} ticket.departmentId - Department ID (optional)
     * @param {number} ticket.assigneeId - Assignee ID (optional)
     * @returns {Promise} - Resolves with created ticket
     */
    createTicket: function(ticket) {
      var self = this;
      
      return new Promise(function(resolve, reject) {
        if (!self.config.token) {
          reject(new Error('Authentication token required'));
          return;
        }
        
        fetch(self.config.apiUrl + '/tickets', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + self.config.token
          },
          body: JSON.stringify(ticket)
        })
        .then(function(response) {
          if (!response.ok) {
            throw new Error('Failed to create ticket');
          }
          return response.json();
        })
        .then(function(data) {
          resolve(data);
        })
        .catch(function(error) {
          reject(error);
        });
      });
    },
    
    /**
     * Get ticket by ID
     * @param {number} ticketId - Ticket ID
     * @returns {Promise} - Resolves with ticket data
     */
    getTicket: function(ticketId) {
      var self = this;
      
      return new Promise(function(resolve, reject) {
        if (!self.config.token) {
          reject(new Error('Authentication token required'));
          return;
        }
        
        fetch(self.config.apiUrl + '/tickets/' + ticketId, {
          method: 'GET',
          headers: {
            'Authorization': 'Bearer ' + self.config.token
          }
        })
        .then(function(response) {
          if (!response.ok) {
            throw new Error('Failed to get ticket');
          }
          return response.json();
        })
        .then(function(data) {
          resolve(data);
        })
        .catch(function(error) {
          reject(error);
        });
      });
    },
    
    /**
     * Get my tickets
     * @param {Object} params - Query parameters
     * @returns {Promise} - Resolves with ticket list
     */
    getMyTickets: function(params) {
      var self = this;
      params = params || {};
      
      var queryString = Object.keys(params)
        .map(function(key) { return key + '=' + encodeURIComponent(params[key]); })
        .join('&');
      
      return new Promise(function(resolve, reject) {
        if (!self.config.token) {
          reject(new Error('Authentication token required'));
          return;
        }
        
        fetch(self.config.apiUrl + '/tickets/me?' + queryString, {
          method: 'GET',
          headers: {
            'Authorization': 'Bearer ' + self.config.token
          }
        })
        .then(function(response) {
          if (!response.ok) {
            throw new Error('Failed to get tickets');
          }
          return response.json();
        })
        .then(function(data) {
          resolve(data);
        })
        .catch(function(error) {
          reject(error);
        });
      });
    },
    
    /**
     * Create a widget element
     * @param {string} containerId - Container element ID
     * @param {Object} options - Widget options
     */
    createWidget: function(containerId, options) {
      options = options || {};
      var container = document.getElementById(containerId);
      
      if (!container) {
        console.error('ITTicketSDK: Container not found');
        return;
      }
      
      var widget = document.createElement('div');
      widget.className = 'it-ticket-widget';
      widget.innerHTML = '\
        <button class="it-ticket-widget-btn">\
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">\
            <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>\
          </svg>\
          <span>' + (options.buttonText || '提交工单') + '</span>\
        </button>\
      ';
      
      var self = this;
      widget.querySelector('.it-ticket-widget-btn').addEventListener('click', function() {
        self.openTicketForm(options);
      });
      
      container.appendChild(widget);
    },
    
    // Private methods
    
    _handleSubmit: function(callback) {
      var form = document.getElementById('it-ticket-form');
      var formData = {
        title: form.querySelector('[name="title"]').value,
        description: form.querySelector('[name="description"]').value,
        type: form.querySelector('[name="type"]').value,
        priority: form.querySelector('[name="priority"]').value
      };
      
      var self = this;
      this.createTicket(formData)
        .then(function(ticket) {
          self.closeTicketForm();
          if (callback) callback(null, ticket);
          self._showNotification('工单创建成功', 'success');
        })
        .catch(function(error) {
          if (callback) callback(error, null);
          self._showNotification('工单创建失败: ' + error.message, 'error');
        });
    },
    
    _getModalHTML: function(options) {
      return '\
        <div class="it-ticket-overlay"></div>\
        <div class="it-ticket-modal-content">\
          <div class="it-ticket-header">\
            <h3>' + (options.title || '提交工单') + '</h3>\
            <button class="it-ticket-close">&times;</button>\
          </div>\
          <form id="it-ticket-form" class="it-ticket-form">\
            <div class="it-ticket-field">\
              <label>标题 <span class="required">*</span></label>\
              <input type="text" name="title" required placeholder="请输入工单标题">\
            </div>\
            <div class="it-ticket-field">\
              <label>描述</label>\
              <textarea name="description" rows="4" placeholder="请详细描述您的问题"></textarea>\
            </div>\
            <div class="it-ticket-row">\
              <div class="it-ticket-field">\
                <label>类型</label>\
                <select name="type">\
                  <option value="BUG">Bug</option>\
                  <option value="FEATURE">功能需求</option>\
                  <option value="TASK">任务</option>\
                  <option value="SUPPORT">支持</option>\
                  <option value="OTHER">其他</option>\
                </select>\
              </div>\
              <div class="it-ticket-field">\
                <label>优先级</label>\
                <select name="priority">\
                  <option value="LOW">低</option>\
                  <option value="MEDIUM" selected>中</option>\
                  <option value="HIGH">高</option>\
                  <option value="CRITICAL">紧急</option>\
                </select>\
              </div>\
            </div>\
          </form>\
          <div class="it-ticket-footer">\
            <button class="it-ticket-cancel">取消</button>\
            <button class="it-ticket-submit">提交</button>\
          </div>\
        </div>\
      ';
    },
    
    _injectStyles: function() {
      if (document.getElementById('it-ticket-styles')) return;
      
      var style = document.createElement('style');
      style.id = 'it-ticket-styles';
      style.textContent = '\
        .it-ticket-overlay {\
          position: fixed;\
          top: 0;\
          left: 0;\
          width: 100%;\
          height: 100%;\
          background: rgba(0, 0, 0, 0.5);\
          z-index: 10000;\
        }\
        .it-ticket-modal-content {\
          position: fixed;\
          top: 50%;\
          left: 50%;\
          transform: translate(-50%, -50%);\
          background: white;\
          border-radius: 8px;\
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);\
          width: 90%;\
          max-width: 500px;\
          max-height: 90vh;\
          overflow-y: auto;\
          z-index: 10001;\
        }\
        .it-ticket-header {\
          display: flex;\
          justify-content: space-between;\
          align-items: center;\
          padding: 16px 20px;\
          border-bottom: 1px solid #e5e7eb;\
        }\
        .it-ticket-header h3 {\
          margin: 0;\
          font-size: 18px;\
          color: #1f2937;\
        }\
        .it-ticket-close {\
          background: none;\
          border: none;\
          font-size: 24px;\
          cursor: pointer;\
          color: #6b7280;\
          padding: 0;\
          line-height: 1;\
        }\
        .it-ticket-close:hover {\
          color: #1f2937;\
        }\
        .it-ticket-form {\
          padding: 20px;\
        }\
        .it-ticket-field {\
          margin-bottom: 16px;\
        }\
        .it-ticket-field label {\
          display: block;\
          margin-bottom: 6px;\
          font-size: 14px;\
          font-weight: 500;\
          color: #374151;\
        }\
        .it-ticket-field .required {\
          color: #ef4444;\
        }\
        .it-ticket-field input,\
        .it-ticket-field textarea,\
        .it-ticket-field select {\
          width: 100%;\
          padding: 10px 12px;\
          border: 1px solid #d1d5db;\
          border-radius: 6px;\
          font-size: 14px;\
          box-sizing: border-box;\
        }\
        .it-ticket-field input:focus,\
        .it-ticket-field textarea:focus,\
        .it-ticket-field select:focus {\
          outline: none;\
          border-color: #3b82f6;\
          box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);\
        }\
        .it-ticket-row {\
          display: flex;\
          gap: 16px;\
        }\
        .it-ticket-row .it-ticket-field {\
          flex: 1;\
        }\
        .it-ticket-footer {\
          display: flex;\
          justify-content: flex-end;\
          gap: 12px;\
          padding: 16px 20px;\
          border-top: 1px solid #e5e7eb;\
        }\
        .it-ticket-footer button {\
          padding: 10px 20px;\
          border-radius: 6px;\
          font-size: 14px;\
          font-weight: 500;\
          cursor: pointer;\
        }\
        .it-ticket-cancel {\
          background: white;\
          border: 1px solid #d1d5db;\
          color: #374151;\
        }\
        .it-ticket-cancel:hover {\
          background: #f9fafb;\
        }\
        .it-ticket-submit {\
          background: #3b82f6;\
          border: 1px solid #3b82f6;\
          color: white;\
        }\
        .it-ticket-submit:hover {\
          background: #2563eb;\
        }\
        .it-ticket-widget {\
          display: inline-block;\
        }\
        .it-ticket-widget-btn {\
          display: inline-flex;\
          align-items: center;\
          gap: 8px;\
          padding: 12px 20px;\
          background: #3b82f6;\
          color: white;\
          border: none;\
          border-radius: 8px;\
          font-size: 14px;\
          font-weight: 500;\
          cursor: pointer;\
        }\
        .it-ticket-widget-btn:hover {\
          background: #2563eb;\
        }\
        .it-ticket-notification {\
          position: fixed;\
          top: 20px;\
          right: 20px;\
          padding: 12px 20px;\
          border-radius: 8px;\
          font-size: 14px;\
          z-index: 10002;\
          animation: slideIn 0.3s ease;\
        }\
        .it-ticket-notification.success {\
          background: #10b981;\
          color: white;\
        }\
        .it-ticket-notification.error {\
          background: #ef4444;\
          color: white;\
        }\
        @keyframes slideIn {\
          from {\
            transform: translateX(100%);\
            opacity: 0;\
          }\
          to {\
            transform: translateX(0);\
            opacity: 1;\
          }\
        }\
      ';
      
      document.head.appendChild(style);
    },
    
    _showNotification: function(message, type) {
      var notification = document.createElement('div');
      notification.className = 'it-ticket-notification ' + type;
      notification.textContent = message;
      document.body.appendChild(notification);
      
      setTimeout(function() {
        notification.remove();
      }, 3000);
    }
  };
  
  // Export to global scope
  global.ITTicketSDK = ITTicketSDK;
  
})(typeof window !== 'undefined' ? window : this);
